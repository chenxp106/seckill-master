package cn.gdut.config;

import cn.gdut.constant.Constant;
import cn.gdut.domain.SeckillUser;
import cn.gdut.service.SeckillUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
public class UserArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    SeckillUserService seckillUserService;

    /**
     * 当请求参数为SeckillUser时，使用这个解析器
     * 客户端的请求到达某个Controller的方法时，判断这个方法的参数是否为SeckillUser
     * 如果是，则这个SeckillUser参数通过下面的resolvrArgument（）方法获得
     * 然后，该control方法继续往下执行时看到的SeckillUser对象就是在这里resolveArgurment（）方法处理过的对象
     * @param methodParameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        Class<?> parameterType = methodParameter.getParameterType();

        return parameterType == SeckillUser.class;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter,
                                  ModelAndViewContainer modelAndViewContainer,
                                  NativeWebRequest nativeWebRequest,
                                  WebDataBinderFactory webDataBinderFactory) throws Exception {
        //获取请求和相应对象
        HttpServletRequest request = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        HttpServletResponse response = nativeWebRequest.getNativeResponse(HttpServletResponse.class);

        //从请求参数中获取token（token可能有两种方式从客户端返回。1 url的参数，2通过set-Cookie字段）
        String parameterToken = request.getParameter(Constant.COOKIE_NAME_TOKEN);
        String cookieToken = getCookieValue(request, Constant.COOKIE_NAME_TOKEN);

        if (StringUtils.isEmpty(parameterToken) && StringUtils.isEmpty(cookieToken)){
            return null;
        }

        //判断是哪种方式获得的token，并由该方式获取token
        String token = StringUtils.isEmpty(parameterToken) ? cookieToken : parameterToken;
        //通过token就能在redis中查找出该token对应的用户对象
        return seckillUserService.getSeckillUserByToken(response,token);
    }

    /**
     * 根据cookie获取相应的cookie值
     * @param request re
     * @param cookieName name
     * @return cookie值
     */
    private String getCookieValue(HttpServletRequest request,String cookieName){
        Cookie[] cookies = request.getCookies();
        if (cookies.length == 0 || cookies == null){
            return null;
        }

        for (Cookie cookie : cookies){
            if (cookie.getName().equals(cookieName)){
                return cookie.getValue();
            }
        }
        return null;
    }
}
