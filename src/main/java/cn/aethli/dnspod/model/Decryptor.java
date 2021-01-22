package cn.aethli.dnspod.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.crypto.Cipher;
import java.security.PrivateKey;

@Getter
@AllArgsConstructor
public class Decryptor {
  private final PrivateKey privateKey;
  private final Cipher cipher;
}
