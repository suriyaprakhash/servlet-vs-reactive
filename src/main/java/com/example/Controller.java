package com.example;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

	@Autowired
	PersonRepo hr;
	
	@RequestMapping("/person")
	public List<Person> getPerson() {
		System.out.println("inside getPerson controller");
		return hr.findAll();
	}
	
}



//cf create-user-provided-service psgservice -p '{"uri":"postgres://postgres:p0stgr3s@bkunjummen-mbp.local:5432/hotelsdb"}'