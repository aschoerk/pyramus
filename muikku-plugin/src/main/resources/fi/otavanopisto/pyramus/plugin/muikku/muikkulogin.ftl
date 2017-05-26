<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
  <head>
    <#include "muikkuloginhead.ftl">
  </head>
  <body class="index">
    <div class="muikku-login-overlay"></div>
    <div class="muikku-login-wrapper">
    <div class="muikku-logo"></div>
    <div class="muikku-login-container">
    <h1>Login in to <br/> Startup high school</h1>
    <form action="login.json" method="post" ix:jsonform="true">
      <div class="formElementRow">
        <input type="text" placeholder="Username" required="required" name="username" id="username">
      </div>      
      <div class="formElementRow">
        <input type="password" placeholder="Password" required="required" name="password" id="password">
      </div>
      
      <div class="formElementRow login">
<!--      
        <#list externalProviders as externalProvider>
          <a class="${externalProvider.name}-button external-login-button" href="?external=${externalProvider.name}"></a>
        </#list>
-->
        <input type="submit" class="login-button" value="Login">
      </div>
    </form>
      <div class="clear"></div>
    </div>
  </div>
</body>
</html>