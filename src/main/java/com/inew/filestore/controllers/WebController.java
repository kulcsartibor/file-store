package com.inew.filestore.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Tibor Kulcsar
 * <p>
 * Date: 4/7/2018
 * @since
 */
//@Controller
public class WebController
{
	@GetMapping(value="/")
	public String homepage(){
		return "index";
	}
}
