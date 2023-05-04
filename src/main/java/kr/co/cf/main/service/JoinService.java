package kr.co.cf.main.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.mail.HtmlEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import kr.co.cf.main.dao.JoinDAO;
import kr.co.cf.main.dto.JoinDTO;

@Service
public class JoinService {
	
	@Autowired JoinDAO dao;
	
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public HashMap<String, Object> idChk(String userId) {
	      
	      HashMap<String, Object> map = new HashMap<String, Object>();
	      logger.info("service userId");
	      int idChk = dao.idChk(userId);
	      map.put("idChk", idChk);
	      return map;
	   }
	
	public HashMap<String, Object> nickChk(String nickName) {
	      
	      HashMap<String, Object> map = new HashMap<String, Object>();
	      logger.info("service nickName");
	      map.put("nickChk", dao.nickChk(nickName));

	      return map;
	   }
	
	
public String write(MultipartFile userProfile, HashMap<String, String> params) {
		logger.info("join service");
	    String msg = "ȸ�����Կ� �����Ͽ����ϴ�.";
	    int locationIdx = locationconf(params);
	    
	    int photoSave = 0;

		JoinDTO dto = new JoinDTO();
		dto.setUserId(params.get("userId"));
		dto.setNickName(params.get("nickName"));
		dto.setUserPw(params.get("userPw"));
		dto.setHeight((params.get("height")));
		dto.setUserName(params.get("userName"));
		dto.setPosition(params.get("position"));
		dto.setLocationIdx(locationIdx);
		Date birthday = Date.valueOf(params.get("birthday"));
		dto.setBirthday(birthday);
		dto.setEmail(params.get("email"));
		dto.setFavTime(params.get("favTime"));
		
		if(!userProfile.getOriginalFilename().equals("")) {
	         logger.info("���� ���ε� �۾�");
	         if(photoSave(userProfile,params) == 1) {
	            photoSave = 1;
	         }
	      }
		
		if(dao.join(dto) == 1) {
			dao.joinData(dto);
	         msg = "ȸ�����Կ� �����Ͽ����ϴ�.";
	      }
	      
	      return msg;
	   }

//����ڰ� �Է��� location�� idx�� �޾ƿ��� �޼���
private int locationconf(HashMap<String, String> params) {
   String location = params.get("location");
   logger.info(location);
   
   int locationIdx = dao.locationFind(location);
   logger.info("locationIdx : "+locationIdx);
   return locationIdx;
}

private int photoSave(MultipartFile userProfile,HashMap<String, String> params) {
    int photoWrite = 0;
    
    // 1. ������ C:/img/upload/ �� ����
          //1-1. ���� �̸� ����
          String oriFileName = userProfile.getOriginalFilename();
          //1-2. ���̸� ����
          String photoName = params.get("userId")+oriFileName;
          logger.info(photoName);
          try {
             byte[] bytes = userProfile.getBytes();//1-3. ����Ʈ ����
             //1-5. ������ ����Ʈ ����
             Path path = Paths.get("C:/img/upload/"+photoName);
             Files.write(path, bytes);
             logger.info(photoName+" save OK");
             // 2. ���� ������ DB �� ����
             //2-1. userProfile, photoName insert
             photoWrite = dao.photoWrite(photoName);
             logger.info("������ ���� ���ε� ����: "+photoWrite);
                      
          } catch (IOException e) {
             e.printStackTrace();
          }
    return photoWrite;
 }

	public JoinDTO login(String id, String pw) {
		
		return dao.login(id,pw);
	}

	public ArrayList<JoinDTO> findId(String email)throws Exception{
		return dao.findId(email);
	}
	
	public int findIdCheck(String email)throws Exception{
		return dao.findIdCheck(email);
	}
	
	// ��й�ȣ ã��
	public void sendEmail(@RequestParam HashMap<String, String> params, String div) throws Exception {
		// Mail Server ����
		String charSet = "utf-8";
		String hostSMTP = "smtp.naver.com"; //���̹� �̿�� smtp.naver.com / ���� ���� smtp.gmail.com
		String hostSMTPid = "jumpxhtna@naver.com";
		String hostSMTPpwd = "ehdgus~9256";

		// ������ ��� EMail, ����, ����
		String fromEmail = "jumpxhtna@naver.com";
		String fromName = "��ٳ���";
		String subject = "";
		String msg = "";

		if(div.equals("findpw")) {
			subject = "��ٳ��� �ӽ� ��й�ȣ �Դϴ�.";
			msg += "<div align='center' style='border:1px solid black; font-family:verdana'>";
			msg += "<h3 style='color: orange;'>";
			msg += params.get("email") + "���� �ӽ� ��й�ȣ �Դϴ�. ��й�ȣ�� �����Ͽ� ����ϼ���.</h3>";
			msg += "<p>�ӽ� ��й�ȣ : ";
			msg += params.get("pw") + "</p></div>";
		}

		// �޴� ��� E-Mail �ּ�
		String mail = params.get("email");
		try {
			HtmlEmail email = new HtmlEmail();
			email.setDebug(true);
			email.setCharset(charSet);
			email.setSSL(true);
			email.setHostName(hostSMTP);
			email.setSmtpPort(587); //���̹� �̿�� 587 / ���� �̿�� 465

			email.setAuthentication(hostSMTPid, hostSMTPpwd);
			email.setTLS(true);
			email.addTo(mail, charSet);
			email.setFrom(fromEmail, fromName, charSet);
			email.setSubject(subject);
			email.setHtmlMsg(msg);
			email.send();
		} catch (Exception e) {
			System.out.println("���Ϲ߼� ���� : " + e);
		}
	}
	
	// �ӽ� ��й�ȣ
	public void findPw(@RequestParam HashMap<String, String> params) throws Exception {
		// �ӽ� ��й�ȣ ����
		
		String pw = "";
		for (int i = 0; i < 12; i++) {
			pw += (char) ((Math.random() * 26) + 97);
		}
		params.put("pw",pw);
		// ��й�ȣ ����
		dao.updatePw(params);
		// ��й�ȣ ���� ���� �߼�
		sendEmail(params, "findpw");
		
		
	}
	
	public int deleteuser(Object removeAttribute) {

	      return dao.userdelete(removeAttribute);
	   }

	   public int userdeletetrue(Object attribute) {

	      return dao.userdelete(attribute);
	   }
}
