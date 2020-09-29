import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Scanner;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class labo1 {

	public static void main(String[] args) throws Exception {

		SecretKey Symmetrickey = SymmetricEncryption.createAESKey();
		// Keypair used for Encryption and Decryption
		KeyPair EncryptionKeys = AsymmetricEncryption.generateRSAKeyPair();
		// Keypair used for Signing and Verifying
		KeyPair SigningKeys = AsymmetricEncryption.generateRSAKeyPair();
		System.out.println("The Symmetric Key is :" + new String(Symmetrickey.getEncoded()));
		System.out.println("The Asymmetric Key is :" + new String(Symmetrickey.getEncoded()));


		Path path = Paths.get("plain-text.txt");
		Scanner scanner = new Scanner(path);
		System.out.println("Read textfile...");
		// read line by line
		while (scanner.hasNextLine()) {
			// process each line
			String testText = scanner.nextLine();
			System.out.println(testText);

			//String testText = "This is a test!";
			// Sender
			// Making the symmetric Key and encrypting the message
			byte[] initial = SymmetricEncryption.createInitializationVector();
			byte[] cipherText = SymmetricEncryption.performAESEncyption(testText, Symmetrickey, initial);

			// Sign the message using the sender's private key
			byte[] messageSignature = DigitalSignature.createDigitalSignature(testText.getBytes(),
					SigningKeys.getPrivate());

			// Encrypting the symmetric Key using the sender's public key
			byte[] encryptedSymmetrickey = AsymmetricEncryption.performRSAEncryption(new String(Symmetrickey.getEncoded()),
					EncryptionKeys.getPublic());

			// Receiver
			String decryptedSymmetrickey = AsymmetricEncryption.performRSADecryption(encryptedSymmetrickey,
					EncryptionKeys.getPrivate());
			// Decrypt message
			byte[] symKey = decryptedSymmetrickey.getBytes();
			SecretKey originalKey = new SecretKeySpec(symKey, 0, symKey.length, "AES");
			System.out.println("The Decrypted Symmetric Key is :" + new String(originalKey.getEncoded()));
			String decryptedMessage = SymmetricEncryption.performAESDecryption(cipherText, originalKey, initial);
			System.out.println(decryptedMessage);
		}
		scanner.close();

	}

}