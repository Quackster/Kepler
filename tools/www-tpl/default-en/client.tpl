<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}:  </title>

<script type="text/javascript">
var andSoItBegins = (new Date()).getTime();
</script>
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="alternate" type="application/rss+xml" title="{{ site.siteName }}: RSS" href="{{ site.sitePath }}/articles/rss.xml" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/visual.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/libs.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/common.js" type="text/javascript"></script>
<script src="{{ site.staticContentPath }}/web-gallery/static/js/fullcontent.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/style.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/buttons.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/boxes.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/tooltips.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/habboclient.css" type="text/css" />
<script src="{{ site.staticContentPath }}/web-gallery/static/js/habboclient.js" type="text/javascript"></script>

<script type="text/javascript">
document.habboLoggedIn = {{ session.loggedIn }};
var habboName = "{{ session.loggedIn ? playerDetails.getName() : "" }}";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
window.name = "client";
if (typeof HabboClient != "undefined") { HabboClient.windowName = "client"; }

</script>

<script type="text/javascript">
var habboClient = true;
HabboClientUtils.init({remoteCallsEnabled : true, taggingGameEnabled : false});
ffec.c("chrome://havvocmini/content/HavvocIcon.PNG");
log(4);
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
<body id="client" class="wide">

<div id="client-topbar" style="display:none">

  <div class="logo"><img src="{{ site.staticContentPath }}/web-gallery/images/popup/popup_topbar_habbologo.gif" alt="" align="middle"/></div>
  <div class="habbocount"><div id="habboCountUpdateTarget">
{{ site.formattedUsersOnline }} members online</div>
	<script language="JavaScript" type="text/javascript">
		setTimeout(function() {
			HabboCounter.init(600);
		}, 20000);
	</script>
</div>
  <div class="logout"><a href="{{ site.sitePath }}/account/logout?origin=popup" onclick="self.close(); return false;">Close Hotel</a></div>
</div>
<noscript>
  <img src="{{ site.sitePath }}/clientlog/nojs" border="0" width="1" height="1" alt="" style="position: absolute; top:0; left: 0"/>
</noscript>

<div id="clientembed-container">
<div id="clientembed-loader" class="loader-image" style="display:none">
    <div class="loader-image-inner">
        <b class="loading-text">Loading {{ site.siteName }}...</b>
    </div>
</div>
<div id="clientembed">
<script type="text/javascript" language="javascript">
	HabboClientUtils.extWrite("<object classid=\"clsid:166B1BCA-3F9C-11CF-8075-444553540000\" codebase=\"http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=10,0,0,0\" id=\"Theme\" width=\"960\" height=\"540\"\>\n<param name=\"src\" value=\"{{ site.loaderDcr }}\"\>\n<param name=\"swRemote\" value=\"swSaveEnabled=\'true\' swVolume=\'true\' swRestart=\'false\' swPausePlay=\'false\' swFastForward=\'false\' swTitle=\'Themehotel\' swContextMenu=\'true\' \"\>\n<param name=\"swStretchStyle\" value=\"stage\"\>\n<param name=\"swText\" value=\"\"\>\n<param name=\"bgColor\" value=\"#000000\"\>\n   <param name=\"sw6\" value=\"client.connection.failed.url={{ site.sitePath }}/clientutils.php?key=connection_failed;external.variables.txt={{ site.loaderVariables }}\"\>\n  {{ forwardScript }}\n <param name=\"sw8\" value=\"use.sso.ticket=1;sso.ticket={{ ssoTicket }}\"\>\n   <param name=\"sw2\" value=\"connection.info.host={{ site.loaderGameIp }};connection.info.port={{ site.loaderGamePort }}\"\>\n   <param name=\"sw9\" value=\"{{ shortcut }}account_id={{ playerDetails.id }}\"\>\n   <param name=\"sw4\" value=\"site.url={{ site.sitePath }};url.prefix={{ site.sitePath }}\"\>\n   <param name=\"sw3\" value=\"connection.mus.host={{ site.loaderMusIp }};connection.mus.port={{ site.loaderMusPort }}\"\>\n   <param name=\"sw1\" value=\"client.allow.cross.domain=1;client.notify.cross.domain=0\"\>\n   <param name=\"sw7\" value=\"external.texts.txt={{ site.loaderTexts }}\"\>\n       <param name=\"sw5\" value=\"client.reload.url={{ site.sitePath }}client.php?x=reauthenticate;client.fatal.error.url={{ site.sitePath }}/clientutils.php?key=error\"\>\n<embed src=\"{{ site.loaderDcr }}\" bgColor=\"#000000\" width=\"960\" height=\"540\" swRemote=\"swSaveEnabled=\'true\' swVolume=\'true\' swRestart=\'false\' swPausePlay=\'false\' swFastForward=\'false\' swTitle=\'Habbo Hotel\' swContextMenu=\'true\'\" swStretchStyle=\"stage\" swText=\"\" type=\"application/x-director\" pluginspage=\"http://www.macromedia.com/shockwave/download/\" \n    sw6=\"client.connection.failed.url={{ site.sitePath }}/clientutils.php?key=connection_failed;external.variables.txt={{ site.loaderVariables }}\"\n  {{ forwardSubScript }} \n    sw8=\"use.sso.ticket=1;sso.ticket={{ ssoTicket }}\"  \n    sw2=\"connection.info.host={{ site.loaderGameIp }};connection.info.port={{ site.loaderGamePort }}\"  \n   sw9=\"{{ shortcut }}account_id={{ playerDetails.id }}\"  \n    sw4=\"site.url={{ site.sitePath }}\;url.prefix={{ site.sitePath }}\"  \n    sw3=\"connection.mus.host={{ site.loaderMusIp }};connection.mus.port={{ site.loaderMusPort }}\"  \n    sw1=\"client.allow.cross.domain=1;client.notify.cross.domain=0\"  \n    sw7=\"external.texts.txt={{ site.loaderTexts }}\"  \n    \n    sw5=\"client.reload.url={{ site.sitePath }}/client.php?x=reauthenticate;client.fatal.error.url={{ site.sitePath }}/clientutils.php?key=error\" \></embed\>\n<noembed\>client.pluginerror.message</noembed\>\n</object\>");
</script>
<noscript>
<object classid="clsid:166B1BCA-3F9C-11CF-8075-444553540000" codebase="http://download.macromedia.com/pub/shockwave/cabs/director/sw.cab#version=10,0,0,0" id="Theme" width="960" height="540">
<param name="src" value="{{ site.loaderDcr }}">
<param name="swRemote" value="swSaveEnabled='true' swVolume='true' swRestart='false' swPausePlay='false' swFastForward='false' swTitle='Themehotel' swContextMenu='true' ">
<param name="swStretchStyle" value="stage">
<param name="swText" value="">
<param name="bgColor" value="#000000">
   <param name="sw6" value="client.connection.failed.url={{ site.sitePath }}/clientutils.php?key=connection_failed;external.variables.txt={{ site.loaderVariables }}">
   {{ forward }}
   <param name="sw9" value="{{ shortcut }}account_id={{ playerDetails.id }}">
   <param name="sw8" value="use.sso.ticket=1;sso.ticket={{ ssoTicket }}">
   <param name="sw2" value="connection.info.host={{ site.loaderGameIp }};connection.info.port={{ site.loaderGamePort }}">
   <param name="sw4" value="site.url={{ site.sitePath }};url.prefix={{ site.sitePath }}">
   <param name="sw3" value="connection.mus.host={{ site.loaderMusIp }};connection.mus.port={{ site.loaderMusPort }}">
   <param name="sw1" value="client.allow.cross.domain=1;client.notify.cross.domain=0">
   <param name="sw7" value="external.texts.txt={{ site.loaderTexts }}">
   <param name="sw5" value="client.reload.url={{ site.sitePath }}/client.php?x=reauthenticate;client.fatal.error.url={{ site.sitePath }}/clientutils.php?key=error">
<!--[if IE]>client.pluginerror.message<![endif]-->
<embed src="{{ site.loaderDcr }}" bgColor="#000000" width="960" height="540" swRemote="swSaveEnabled='true' swVolume='true' swRestart='false' swPausePlay='false' swFastForward='false' swTitle='Themehotel' swContextMenu='true'" swStretchStyle="stage" swText="" type="application/x-director" pluginspage="http://www.macromedia.com/shockwave/download/"
    {{ forwardSub }}
    sw6="client.connection.failed.url={{ site.sitePath }}/clientutils.php?key=connection_failed;external.variables.txt={{ site.loaderVariables }}"
	sw9="{{ shortcut }}account_id={{ playerDetails.id }}"
    sw8="use.sso.ticket=1;sso.ticket={{ ssoTicket }}"
    sw2="connection.info.host={{ site.loaderGameIp }};connection.info.port={{ site.loaderGamePort }}"
    sw4="site.url={{ site.sitePath }};url.prefix={{ site.sitePath }}"
    sw3="connection.mus.host={{ site.loaderMusIp }};connection.mus.port={{ site.loaderMusPort }}"
    sw1="client.allow.cross.domain=1;client.notify.cross.domain=0"
    sw7="external.texts.txt={{ site.loaderTexts }}"
	    sw5="client.reload.url={{ site.sitePath }}/client.php?x=reauthenticate;client.fatal.error.url={{ site.sitePath }}/clientutils.php?key=error" ></embed>
<noembed>client.pluginerror.message</noembed>
</object>
</noscript>

</div>
<script type="text/javascript">
HabboClientUtils.loaderTimeout = 10 * 1000;
HabboClientUtils.showLoader(["Loading {{ site.siteName }}", "Loading {{ site.siteName }}.", "Loading {{ site.siteName }}..", "Loading {{ site.siteName }}..."]);
</script>

<script type="text/javascript" language="javascript">
try {
var _shockwaveDetectionSuccessful = true;
_shockwaveDetectionSuccessful = ShockwaveInstallation.swDetectionCheck();
if (!_shockwaveDetectionSuccessful) {
    log(50);
}
if (_shockwaveDetectionSuccessful) {
  HabboClientUtils.cacheCheck();
}
} catch(e) {
    try {
		HabboClientUtils.logClientJavascriptError(e);
	} catch(e2) {}
}
</script>

</body>
</html>