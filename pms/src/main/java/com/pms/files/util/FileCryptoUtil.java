package com.pms.files.util;

import java.io.InputStream;
import java.io.OutputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileCryptoUtil {
	private final String ALGORITHM = "AES/CBC/PKCS5Padding";
	private SecretKeySpec KEY;
	private IvParameterSpec IV;

	// Key, Iv 주입
	public FileCryptoUtil(@Value("${file.crypto.key}") String key, @Value("${file.crypto.iv}") String iv) {
		this.KEY = new SecretKeySpec(key.getBytes(), "AES");
		this.IV = new IvParameterSpec(iv.getBytes());
	}

	// 암호화
	public void encrypt(InputStream is, OutputStream os) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, KEY, IV);

		try (CipherInputStream cis = new CipherInputStream(is, cipher)) {
			cis.transferTo(os);
		}
	}

	// 복호화
	public void decrypt(InputStream is, OutputStream os) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, KEY, IV);

		try (CipherInputStream cis = new CipherInputStream(is, cipher)) {
			cis.transferTo(os);
		}
	}

	// zip
	public void decryptZip(InputStream is, OutputStream os) throws Exception {
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, KEY, IV);
		
		// CipherInputStream을 사용하여 zos를 닫지 않고 데이터만 전달
		// zos.close()는 zip 반복문 작업 후 호출됨 
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[8192];
		int read;
		while ((read = cis.read(buffer)) != -1) {
			os.write(buffer, 0, read);
		}
	}
}
