import requests
import re
import json
import os
import base64
from Crypto.PublicKey import RSA
from Crypto.Cipher import PKCS1_v1_5
import sys

def rsa_segment_encrypt(pk, plaintext):
    plaintext = plaintext.encode()
    length = len(plaintext)
    default_length = 256-11
    if length < default_length:
        return base64.b64encode(pk.encrypt(plaintext)).decode()
    offset = 0
    res = []
    while length - offset > 0:
        if length - offset > default_length:
            res.append(pk.encrypt(plaintext[offset:offset+default_length]))
        else:
            res.append(pk.encrypt(plaintext[offset:]))
        offset += default_length
    byte_data = b''.join(res)

    return base64.b64encode(byte_data).decode()

def encrypt(plaintext):
    key = open('publicKey.txt').read()
    publickey = RSA.importKey(base64.b64decode(key))
    pk = PKCS1_v1_5.new(publickey)
    return rsa_segment_encrypt(pk,plaintext)

def checkLastIp(currentIp):
    file=None
    try:
        file = open('./' + 'lastIp' + '.json','r+')
    except FileNotFoundError as e:
        file = open('./' + 'lastIp' + '.json','w+')
    try:
        lastIpJson=json.load(file)
        if lastIpJson.get('ip')!=currentIp:
            content={'ip':currentIp}
            file.seek(0)
            file.truncate()
            json.dump(content,fp=file)
            return False
        else:
            return True
    except json.decoder.JSONDecodeError as e:
        content={'ip':currentIp}
        json.dump(content,fp=file)
    file.close()
    return False

def checkRecord(configJson,ip):
    url=configJson.get('api')+'/security/record/request'

    msg={
        'Action': 'RecordList'
    }
    msg['subDomain']=configJson.get('subDomain')
    msg['domain']=configJson['domain']
    msg['SignatureMethod']=configJson['SignatureMethod']
    msg['SecretId']=configJson['SecretId']
    msg['SecretKey']=configJson['SecretKey']

    msg=json.dumps(msg)
    content=encrypt(msg)

    noticeMsg={
        'key':configJson['key'],
        'content':content
    }
    headers = {'Content-Type': 'application/json'}
    response=requests.post(url=url,data=json.dumps(noticeMsg),headers=headers)
    result=json.loads(response.text)
    # print(response.text)
    records=result['data']['data']['records']
    if len(records):
        result=False
        for record in records:
            if record['name']==configJson.get('subDomain') and record['value']==ip:
                result=True
        if result:
            return 0,None
        else:
            return 1,record['id']
    else:
        return 2,None

def addRecord(configJson,ip):
    url=configJson.get('api')+'/security/record/notice/request'
    msg={
        'subject':'RecordCreate',
        'data':{
            'Action': 'RecordCreate'
        }
    }
    msg['to']=configJson['to']
    msg['data']['subDomain']=configJson.get('subDomain')
    msg['data']['domain']=configJson['domain']
    msg['data']['SignatureMethod']=configJson['SignatureMethod']
    msg['data']['SecretId']=configJson['SecretId']
    msg['data']['SecretKey']=configJson['SecretKey']
    msg['data']['recordType']=configJson['recordType']
    msg['data']['recordLine']=configJson['recordLine']
    msg['data']['value']=ip

    msg=json.dumps(msg)
    content=encrypt(msg)

    noticeMsg={
        'key':configJson['key'],
        'content':content
    }
    headers = {'Content-Type': 'application/json'}
    response=requests.post(url=url,data=json.dumps(noticeMsg),headers=headers)
    result=json.loads(response.text)

def modifyRecord(configJson,ip,id):
    url=configJson.get('api')+'/security/record/notice/request'
    msg={
        'subject':'RecordModify',
        'data':{
            'Action': 'RecordModify'
        }
    }
    msg['to']=configJson['to']
    msg['data']['subDomain']=configJson.get('subDomain')
    msg['data']['domain']=configJson['domain']
    msg['data']['SignatureMethod']=configJson['SignatureMethod']
    msg['data']['SecretId']=configJson['SecretId']
    msg['data']['SecretKey']=configJson['SecretKey']
    msg['data']['recordType']=configJson['recordType']
    msg['data']['recordLine']=configJson['recordLine']
    msg['data']['value']=ip
    msg['data']['recordId']=id

    msg=json.dumps(msg)
    content=encrypt(msg)

    noticeMsg={
        'key':configJson['key'],
        'content':content
    }
    headers = {'Content-Type': 'application/json'}
    response=requests.post(url=url,data=json.dumps(noticeMsg),headers=headers)
    result=json.loads(response.text)

if __name__ == '__main__':
    with open('./config.json','r',encoding='utf8')as fp:
        configJson=json.load(fp)
        url = configJson['api']+'/ip/myIp'
        response = requests.get(url)
        page_text = response.text
        result=re.search('((?:[0-9]{1,3}\.){3}[0-9]{1,3})', page_text)
        ip=result.group(1)
        if not result:
            print('cannot find ip')
            sys.exit(1)
        print('current ip: '+ip)
        if checkLastIp(ip):
            print('ip unchanged')
        status,id=checkRecord(configJson,ip)
        if status == 0:
            print('record unchanged, dnsed successfully!')
        elif status == 1:
            print('record was old, add record task start...')
            modifyRecord(configJson,ip,id)
        elif status == 2:
            print('record has not been dnsed, modify record task start...')
            addRecord(configJson,ip)