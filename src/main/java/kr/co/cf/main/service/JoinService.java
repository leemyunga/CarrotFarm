package kr.co.cf.main.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.text.DateFormat;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

}
