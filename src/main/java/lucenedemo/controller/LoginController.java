package lucenedemo.controller;

import lucenedemo.core.exception.BusinessException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import lucenedemo.entity.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;

@RestController
@Api(tags = "login接口")
@RequestMapping("/login")
public class LoginController {

	
	
	@ApiOperation(value = "登录")
	@PostMapping(value = "/doLogin")
	public void doLogin(@RequestBody User user, HttpServletRequest request) {

		if (("admin".equals(user.getUserName()) && "root".equals(user.getPassword()))) {
			request.getSession().setAttribute("user", user);
		} else {
			throw new BusinessException("用户名或密码错误");
		}

	}
	
	@ApiOperation(value = "退出登录")
	@PostMapping(value = "/doLogOut")
	public void doLogOut(HttpServletRequest request) {
		request.getSession().removeAttribute("user");

	}

}
