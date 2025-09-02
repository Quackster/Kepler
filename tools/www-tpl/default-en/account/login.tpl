
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Log in to {{ site.siteName }} </title>

<script type="text/javascript">
var andSoItBegins = (new Date()).getTime();
</script>
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="alternate" type="application/rss+xml" title="{{ site.siteName }}: RSS" href="{{ site.sitePath }}/articles/rss.xml" />

<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/landing.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/style.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/buttons.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/boxes.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/tooltips.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/local/com.css" type="text/css" />

<script src="{{ site.staticContentPath }}/web-gallery/js/local/com.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/process.css" type="text/css" />

<script type="text/javascript">
document.habboLoggedIn = false;
var habboName = null;
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
var habboDefaultClientPopupUrl = "{{ site.sitePath }}/client";
window.name = "habboMain";
if (typeof HabboClient != "undefined") { HabboClient.windowName = "client"; }

</script>


<meta name="description" content="Join the world's largest virtual hangout where you can meet and make friends. Design your own rooms, collect cool furniture, throw parties and so much more! Create your FREE {{ site.siteName }} today!" />
<meta name="keywords" content="{{ site.siteName }}, virtual, world, join, groups, forums, play, games, online, friends, teens, collecting, social network, create, collect, connect, furniture, virtual, goods, sharing, badges, social, networking, hangout, safe, music, celebrity, celebrity visits, cele" />

<!--[if IE 8]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie8.css" type="text/css" />
<![endif]-->
<!--[if lt IE 8]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie.css" type="text/css" />
<![endif]-->
<!--[if lt IE 7]>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/ie6.css" type="text/css" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/pngfix.js" type="text/javascript"></script>
<script type="text/javascript">
try { document.execCommand('BackgroundImageCache', false, true); } catch(e) {}
</script>

<style type="text/css">
body { behavior: url({{ site.staticContentPath }}/web-gallery/js/csshover.htc); }
</style>
<![endif]-->
<meta name="build" content="HavanaWeb" />
</head>
<body id="popup" class="process-template">

<div id="overlay"></div>

<div id="container">
	<div class="cbb process-template-box clearfix">
		<div id="content">
			{% include "../base/frontpage_header.tpl" %}
			<div id="process-content">
	        	<div id="column1" class="column">
				<div class="habblet-container ">		
	
						<div class="cbb clearfix green">
    <h2 class="title">Register for free</h2>
    <div class="box-content">
        <p>Register for free by clicking the Create Your Habbo -button below. If you have already registered, please sign in on the right.</p>
        <div class="register-button clearfix">
            <a href="{{ site.sitePath }}/register" onclick="HabboClient.closeHabboAndOpenMainWindow(this); return false;">Create Your Habbo »</a>
            <span></span>
        </div>                
    </div>
</div>
	
						
					
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 
</div>
<div id="column2" class="column">
			     		
				<div class="habblet-container ">		
	
						<div class="logincontainer">

<div class="cbb loginbox clearfix">
    <h2 class="title">Sign in</h2>       
    <div class="box-content clearfix" id="login-habblet">
        <form action="{{ site.sitePath }}/account/submit" method="post" class="login-habblet">
            
            <ul>

                <li>
                    <label for="login-username" class="login-text">Email</label>
                    <input tabindex="1" type="text" class="login-field" name="username" id="login-username" value="" maxlength="48"/>
                </li>

                <li>
                    <label for="login-password" class="login-text">Password</label>
                    <input tabindex="2" type="password" class="login-field" name="password" id="login-password" maxlength="32"/>
                    <input type="submit" value="Login" class="submit" id="login-submit-button"/>
                    <a href="#" id="login-submit-new-button" class="new-button" style="margin-left: 0;display:none"><b style="padding-left: 10px; padding-right: 7px; width: 55px">Login</b><i></i></a>
                </li>

                <li id="remember-me" class="no-label">
                    <input tabindex="4" type="checkbox" name="_login_remember_me" id="login-remember-me" value="true"/>
                    <label for="login-remember-me">Keep me logged in</label>
                </li>
                <li id="register-link" class="no-label">
                    <a href="{{ site.sitePath }}/register" class="login-register-link" onclick="HabboClient.closeHabboAndOpenMainWindow(this); return false;"><span>Register for free</span></a>
                </li>
                <li class="no-label">
                    <a href="{{ site.sitePath }}/account/password/forgot" id="forgot-password"><span>Forgot your password?</span></a>
                </li>
            </ul>
<div id="remember-me-notification" class="bottom-bubble" style="display:none;">
	<div class="bottom-bubble-t"><div></div></div>
	<div class="bottom-bubble-c">
                By selecting this you will stay logged in to Habbo, until you &quot;Log out&quot;.
	</div>
	<div class="bottom-bubble-b"><div></div></div>
</div>
        </form>

    </div>
</div>
</div>
<script type="text/javascript">
L10N.put("authentication.form.name", "Email");
L10N.put("authentication.form.password", "Password");
HabboView.add(function() {LoginFormUI.init();});
HabboView.add(function() {window.setTimeout(function() {RememberMeUI.init("newfrontpage");}, 100)});
</script>
	
						
					
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->

{% include "../base/footer.tpl" %}

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>