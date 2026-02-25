package com.pms.files;

import static org.junit.Assert.assertArrayEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.pms.files.util.FileCryptoUtil;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
public class FilesCryptoUtilTest {

	@Autowired
	private FileCryptoUtil fileCryptoUtil;

	@Test
	@DisplayName("파일 암호화/복호화 무결성 테스트")
	public void fileCryptoTest() throws Exception {
		System.out.println("[Crypto] FILE CRYPTO TEST START");
		
		// 파일 경로
		Path orgPath = Paths.get("src/test/resources/upload/test_01.png");
		byte[] orgHash = MessageDigest.getInstance("SHA-256").digest(Files.readAllBytes(orgPath));

		// 암호화
		ByteArrayOutputStream enOs = new ByteArrayOutputStream();
		try (InputStream is = new FileInputStream(orgPath.toFile())) {
			fileCryptoUtil.encrypt(is, enOs);
		}

		// 복호화
		ByteArrayInputStream enIs = new ByteArrayInputStream(enOs.toByteArray());
		ByteArrayOutputStream deOs = new ByteArrayOutputStream();
		fileCryptoUtil.decrypt(enIs, deOs);

		// 검증
		byte[] deHash = MessageDigest.getInstance("SHA-256").digest(deOs.toByteArray());
		assertArrayEquals("파일이 일치하지 않습니다.", orgHash, deHash);
		
		System.out.println("[Crypto] FILE CRYPTO TEST END");
	}
}
