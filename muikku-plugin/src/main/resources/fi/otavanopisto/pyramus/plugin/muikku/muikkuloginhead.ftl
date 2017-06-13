<meta content="width=device-width, initial-scale=1.0, minimun-scale=1" name="viewport" />
<link href='https://fonts.googleapis.com/css?family=Open+Sans:400,300,600&subset=latin,latin-ext,cyrillic-ext,cyrillic,greek,greek-ext' rel='stylesheet' type='text/css'/>

<@include_page path="/templates/generic/head_generic.jsp"/>
<@include_page path="/templates/generic/jsonrequest_support.jsp"/>
<@include_page path="/templates/generic/jsonform_support.jsp"/>
<@include_page path="/templates/generic/scriptaculous_support.jsp"/>
<@include_page path="/templates/generic/message_adapter_support.jsp"/>

<style type="text/css">

@font-face {
  font-family: 'Axis';
  src: url('//cdn.muikkuverkko.fi/fonts/Axis/1.0.0/Axis.eot'); /* IE9 Compat Modes */
  src: url('//cdn.muikkuverkko.fi/fonts/Axis/1.0.0/Axis.eot?#iefix') format('embedded-opentype'), /* IE6-IE8 */
       url('//cdn.muikkuverkko.fi/fonts/Axis/1.0.0/Axis.woff') format('woff'), /* Pretty Modern Browsers */
       url('//cdn.muikkuverkko.fi/fonts/Axis/1.0.0/Axis.ttf')  format('truetype'), /* Safari, Android, iOS */
       url('//cdn.muikkuverkko.fi/fonts/Axis/1.0.0/Axis.svg#svgFontName') format('svg'); /* Legacy iOS */
}

@import url('https://fonts.googleapis.com/css?family=Gentium+Basic')

  body {
    font-family: "Gentium Basic";
    font-size: 14px;
    line-height: 1.5em;
    margin: 0;
    padding: 0 0 30px;
  }   
  
  h1, h2, h3, h4, h5 {
    font-family: "Axis",Verdana,sans-serif;
    color: #6f1fee;
    margin: 0 0 1em;
    text-transform: uppercase;
    text-align: center;
  }
  
  .formLabel, label {
    color: #6f1fee;
    font-family: "Axis",Verdana,sans-serif;
    display: block;
    padding: 5px 5px 5px 0;
  }
  
  .clear {
    clear: both;
    display: block;
    height: 0;
    overflow: hidden;
    visibility: hidden;
    width: 0;
  }
  
  .dialogGlassPane {
    z-index:1998;
    opacity:0.4;
    background:#000;
  }
  
  .IxDialog {
    z-index:1999;
    font-size:16px;
    text-transform:uppercase;
    background:#fff;
    border-radius:0;
    border:0px;
  }
  
  .IxDialog .IxDialogTitleBar {
    margin:0 0 10px 0;
  }
  
  .IxDialog .IxDialogTitleBar .IxDialogTitle {
    background:#7391a7;
    padding:10px;
  }
  
  .IxDialog .IxDialogButtonsContainer {
    margin:10px 5px 5px 5px;
  }
  
  .IxDialog .IxDialogButton {
    font-size:16px;
    text-transform:uppercase;
    border-radius:2px;
    padding:4px 8px;
    background:#7391a7;
  }
  
  .muikku-login-overlay {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
    z-index: 998;
  }
  
  .muikku-login-wrapper {
    position: absolute;
    top: 50%;
    left: 50%;
    margin: -240px 0 0 -270px;
    width:500px;
    z-index: 999;
  }
  
  .muikku-login-wrapper .muikku-logo {
    background-image: url('//cdn.muikkuverkko.fi/assets/startup/up-logo.png');
    background-repeat: no-repeat;
    background-position: center;
    position: relative;
    left: 0px;
    top: 0;
    height: 110px;
    background-size: 105px 70px;    
    

  }
  
  .muikku-login-wrapper .muikku-login-container {
    background: #fff;
    padding: 20px;
    border-radius: 10px;
  }
  
  .muikku-login-wrapper .muikku-login-container h1 {
    font-weight: 300;
    font-size: 26px;
  }
  
  .muikku-login-wrapper .muikku-login-container .formElementRow {
    margin: 10px 0 0 0;
  }
  
  .muikku-login-wrapper .muikku-login-container .formElementRow label {
    font-weight: 300;
    text-transform: uppercase;
    font-size: 16px;
    margin-right: 10px;
    display: block;
  }
  
  .muikku-login-wrapper .muikku-login-container .formElementRow input[type="text"],
  .muikku-login-wrapper .muikku-login-container .formElementRow input[type="password"] {
    font-family: "Gentium Basic";
    text-align: center;
    font-size: 18px;
    font-weight: 300;
    border-radius: 0;
    padding: 8px 16px;
    line-height: 2em;
    box-sizing: border-box;
    width: 100%;
    background-color: #fff;
    border: none;
    border-bottom: 1px solid #6f1fee;
    color: #6f1fee;
    height: auto;
  }
  
    
  .muikku-login-wrapper .muikku-login-container .formElementRow.login {
    display: flex;
    justify-content: center;
    
  } 
  
  .muikku-login-wrapper .muikku-login-container .formElementRow input[type=submit].login-button {
    background: #6f1fee none repeat scroll 0 0;
    font-family: "Axis",Verdana,sans-serif;
    border: 0 none;
    border-radius: 0;
    color: #fff;
    float: right;
    letter-spacing: 2px;
    font-size: 15px;
    font-weight: 300;
    line-height: 2em;
    margin: 10px 0 0 10px;
    padding: 2px 28px;
    cursor: pointer;
    display: inline-block;
    text-transform: uppercase;
  }
  .muikku-login-wrapper .muikku-login-container .formElementRow a.external-login-button {
    font-size: 18px;
    font-weight: 300;
    padding: 16px 16px;
    border-radius: 3px;
    border: 0;
    background: #7391a7;
    color: #fff;
    line-height: 2em;
    text-decoration: none;
    text-transform: uppercase;
    display: inline-block;
    float: left;
    margin-top: 20px;
  }    
  
  .muikku-login-wrapper .muikku-login-container .formElementRow a.Google-oauth-button {
    background: rgba(0, 0, 0, 0) url("../gfx/icons/32x32/google-auth-icon.png") no-repeat scroll 0 0;
  }
  
  @media screen and (max-width: 767px) {
    .muikku-login-wrapper {
      height:430px;
      left:10px;
      margin:-230px auto 0;
      max-width:500px;
      min-width:300px;
      position:absolute;
      right:10px;
      top:50%;
      width:auto;
      z-index:999;
    }
    .muikku-login-wrapper .muikku-logo {
      background-image: url('//cdn.muikkuverkko.fi/assets/startup/up-logo.png');
      background-position:center top;
      background-repeat: no-repeat;
      height: 90px;
      max-width:500px;
      min-width:300px;
      width:auto;
      position: relative;
      z-index: 999;
    }

  }
</style>