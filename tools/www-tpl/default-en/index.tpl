
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Home </title>

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
<body id="landing" class="process-template">

<div id="overlay"></div>

<div id="container">
	<div class="cbb process-template-box clearfix">
		<div id="content">
			{% include "base/frontpage_header.tpl" %}		
			<div id="process-content">
	        	<div id="column1" class="column">
				<div class="habblet-container " id="create-habbo">

						<div id="create-habbo-flash">
	<div id="create-habbo-nonflash" style="background-image: url({{ site.staticContentPath }}/web-gallery/v2/images/landing/landing_group.png)">
        <div id="landing-register-text"><a href="register"><span>Join now, it's free >></span></a></div>
        <div id="landing-promotional-text"><span>{{ site.siteName }} is a virtual world where you can meet and make friends.</span></div>
    </div>
	<div class="cbb clearfix green" id="habbo-intro-nonflash">
		<h2 class="title">To get most out of {{ site.siteName }}, do this:</h2>
		<div class="box-content">
			<ul>
				<li id="habbo-intro-install" style="display:none"><a href="http://www.adobe.com/go/getflashplayer">Install Flash Player 8 or higher</a></li>
				<noscript><li>Enable JavaScript</li></noscript>
			</ul>
		</div>
	</div>
</div>

<script type="text/javascript" language="JavaScript">
var swfobj = new SWFObject("{{ site.staticContentPath }}/web-gallery/flash/intro/habbos.swf", "ch", "396", "378", "8");
swfobj.addParam("AllowScriptAccess", "always");
swfobj.addParam("wmode", "transparent");
swfobj.addVariable("base_url", "{{ site.staticContentPath }}/web-gallery/flash/intro");
swfobj.addVariable("habbos_url", "{{ site.staticContentPath }}/xml/promo_habbos.xml");
swfobj.addVariable("create_button_text", "Register today! &raquo;");
swfobj.addVariable("in_hotel_text", "Online now!");
swfobj.addVariable("slogan", "{{ site.siteName }} Hotel is a virtual world where you can meet and make friends!");
swfobj.addVariable("video_start", "PLAY VIDEO");
swfobj.addVariable("video_stop", "STOP VIDEO");
swfobj.addVariable("button_link", "register");
swfobj.addVariable("localization_url", "{{ site.staticContentPath }}/xml/landing_intro.xml");
swfobj.addVariable("video_link", "{{ site.staticContentPath }}/web-gallery/flash/intro/Habbo_intro.swf");
swfobj.write("create-habbo-flash");
HabboView.add(function() {
	if (deconcept.SWFObjectUtil.getPlayerVersion()["major"] >= 8) {
		try { $("habbo-intro-nonflash").hide(); } catch (e) {}
	} else {
		$("habbo-intro-install").show();
	}
});
var PromoHabbos = { track:function(n) { if (!!n && window.pageTracker) { pageTracker._trackPageview("/landingpromo/" + n); } } }
</script>



				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
<div id="column2" class="column">
				<div class="habblet-container ">
				{% if alert.hasAlert %}
				<div class="action-error flash-message">
 <div class="rounded-container"><div style="background-color: rgb(255, 255, 255);"><div style="margin: 0px 4px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(238, 107, 122);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(231, 40, 62);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(227, 8, 33);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div></div></div><div style="margin: 0px 2px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(238, 105, 121);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 1, 27);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div></div><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(233, 64, 83);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(238, 105, 121);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 1, 27);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(238, 107, 122);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(231, 40, 62);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(227, 8, 33);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div><div class="rounded-done">
  <ul>
   <li>{{ alert.message }}</li>
  </ul>
 </div><div style="background-color: rgb(255, 255, 255);"><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(227, 8, 33);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(231, 40, 62);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(238, 107, 122);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 1, 27);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(238, 105, 121);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div><div style="margin: 0px 1px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(233, 64, 83);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div><div style="margin: 0px 2px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(238, 105, 121);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 1, 27);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div></div><div style="margin: 0px 4px; height: 1px; overflow: hidden; background-color: rgb(255, 255, 255);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(238, 107, 122);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(231, 40, 62);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(227, 8, 33);"><div style="height: 1px; overflow: hidden; margin: 0px 1px; background-color: rgb(226, 0, 26);"></div></div></div></div></div></div></div>
</div>
				{% endif %}
						<div class="cbb loginbox clearfix">
    <h2 class="title">Sign in</h2>

    <div class="box-content clearfix" id="login-habblet">
        <form action="{{ site.sitePath }}/account/submit" method="post" class="login-habblet">
			            <ul>
                <li>
                    <label for="login-username" class="login-text">Username</label>
                    <input tabindex="1" type="text" class="login-field" name="username" id="login-username" value="{{ username }}"/>
                </li>
                <li>
                    <label for="login-password" class="login-text">Password</label>
                    <input tabindex="2" type="password" class="login-field" name="password" id="login-password" />
	                <input type="submit" value="Sign in" class="submit" id="login-submit-button"/>
	                <a href="#" id="login-submit-new-button" class="new-button" style="float: left; margin-left: 0;display:none"><b style="padding-left: 10px; padding-right: 7px; width: 55px">Sign in</b><i></i></a>
                </li>
                <li class="no-label">
										{% if rememberMe %}
                    <input tabindex="3" type="checkbox" value="true" name="_login_remember_me" id="login-remember-me" checked="true"/>
										{% else %}
                    <input tabindex="3" type="checkbox" value="true" name="_login_remember_me" id="login-remember-me"/>
										{% endif %}
										<label for="login-remember-me">Remember me</label>
                </li>
                <li class="no-label">
                    <a href="{{ site.sitePath }}/register" class="login-register-link"><span>Register for free</span></a>
                </li>
                <li class="no-label">
                    <a href="{{ site.sitePath }}/account/password/forgot" id="forgot-password"><span>I forgot my username/password</span></a>
                </li>
            </ul>
        </form>

    </div>
</div>
<div id="remember-me-notification" class="bottom-bubble" style="display:none;">
	<div class="bottom-bubble-t"><div></div></div>
	<div class="bottom-bubble-c">
	By selecting 'remember me' you will stay signed in on this computer until you click 'Sign Out'. If this is a public computer please do not use this feature.	</div>
	<div class="bottom-bubble-b"><div></div></div>
</div>
<script type="text/javascript">
	HabboView.add(LoginFormUI.init);
	HabboView.add(RememberMeUI.init);
</script>



								</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

				<div class="habblet-container ">

						<div class="ad-container">
<div id="geoip-ad" style="display:none"></div>
</div>



				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

				<div class="habblet-container ">





				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

				<div class="habblet-container "> <!--
	<div class="rounded" style="background-color: orange; color: white">
			<strong>Attention!</strong><br />
			This server has been wiped since December 11th, 2019 due to an unfortunate incident.<br />
		</div>
		<br /> -->
				
						<div class="ad-container">
<!-- <a href="{{ site.sitePath }}/register"><img src="{{ site.staticContentPath }}/web-gallery/v2/images/landing/filler_ad.png" alt="" /></a> -->
<a href="{{ site.sitePath }}/games"><img src="{{ site.staticContentPath }}/web-gallery/v2/images/landing/uk_party_frontpage_image.gif" alt="" /></a>

</div>
	
						
					
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
<div id="column3" class="column">
</div>
<div id="column-footer">
		
				<div class="habblet-container ">		
	
						<!-- <div class="habblet box-content" id="tag-cloud-slim">
    <span class="tags-habbos-like">{{ site.siteName }}s Like..</span>
No tags to display.</div> -->

				<div class="habblet box-content" id="tag-cloud-slim">
				<span class="tags-habbos-like">{{ site.siteName }}s Like..</span>
				{% autoescape 'html' %}
					{% if tagCloud|length > 0 %}
						{% for kvp in tagCloud %}
							{% set tag = kvp.getKey() %}
							{% set size = kvp.getValue() %}
							<ul class="tag-list">
								<li><a href="{{ site.sitePath }}/tag/{{ tag }}" class="tag" style="font-size:{{ size }}px">{{ tag }}</a> </li>
							</ul>
						{% endfor %}
					{% else %}
						No tags to display.
					{% endif %}	
				{% endautoescape %}
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 
 
<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->

{% include "base/footer.tpl" %}

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>