/*package api;

import static com.mybank.pc.kits.RSAKit.decrypt;
import static com.mybank.pc.kits.RSAKit.encrypt;
import static com.mybank.pc.kits.RSAKit.getKeyString;
import static com.mybank.pc.kits.RSAKit.getPrivateKey;
import static com.mybank.pc.kits.RSAKit.getPublicKey;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.mybank.pc.collection.api.CollAPIRSAKey;
import com.mybank.pc.kits.RSAKit;

public class RSATest {
	public static void keyTest() throws Exception {
		PublicKey publicKey = getPublicKey(CollAPIRSAKey.COLL_CLIENT.getPublicKey());
		PrivateKey privateKey = getPrivateKey(CollAPIRSAKey.COLL_CLIENT.getPrivateKey());

		String publicKeyString = getKeyString(publicKey);
		System.out.println("public:\n" + publicKeyString);

		String privateKeyString = getKeyString(privateKey);
		System.out.println("private:\n" + privateKeyString);

		String json = "{\"Name\":\"测试用户名\",\"PapersNum\":\"110105198407130034\",\"CustomerID\":\"18042011133802900001\",\"service\":\"n0006Service\",\"CreditProtocolID\":\"\",\"PapersType\":\"1\",\"N\":\"N测试用户名\",\"Na\":\"Na测试用户名\"}";
		System.out.println("准备用公钥加密的字符串为：" + json);
		String cryptograph = encrypt(json, publicKey);// 生成的密文
		System.out.println("用公钥加密后的结果为:" + cryptograph);
		String target = decrypt(cryptograph, privateKey);// 解密密文
		System.out.println("用私钥解密后的结果为：" + target);

	}

	public static void test() throws Exception {
		String s = "Y3hynryzYQupeNetophpABwYqGec+QdHBvf6bYgt6+cIgwafIk+bC+i65QWqjjRb/nt1QmfmNCHwOYRjs62ux2Mwmgh08NzgtoDjR1QQX164M0CKuWJ9eki3pwl6RvDsOpUExnsi3o6JLPjIQaOqpc7dlcCt3fpNkuYc9kweBxg=";
		String decryptReq = RSAKit.decrypt(s, CollAPIRSAKey.COLL_CLIENT.getPrivateKey());
		System.out.println(decryptReq);
	}

	public static void main(String[] args) throws Exception {
		//keyTest();
		test();
	}
}*/
