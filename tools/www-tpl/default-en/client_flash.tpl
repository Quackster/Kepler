
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en" xmlns:og="http://opengraphprotocol.org/schema/" xmlns:fb="http://www.facebook.com/2008/fbml">

<head>
    <meta http-equiv="content-type" content="text/html" />
    <title>{{ site.siteName }}: Hotel</title>

    <script type="text/javascript">
        var andSoItBegins = (new Date()).getTime();
    </script>
	
    <link rel="shortcut icon" href="{{ site.staticContentPath }}/web-gallery/v2/favicon.ico" type="image/vnd.microsoft.icon" />
    <link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/static/styles/common.css" type="text/css" />
    <script src="{{ site.staticContentPath }}/web-gallery/static/js/libs2.js" type="text/javascript"></script>
    <script src="{{ site.staticContentPath }}/web-gallery/static/js/visual.js" type="text/javascript"></script>
    <script src="{{ site.staticContentPath }}/web-gallery/static/js/libs.js" type="text/javascript"></script>
    <script src="{{ site.staticContentPath }}/web-gallery/static/js/common.js" type="text/javascript"></script>
    <script type="text/javascript">
        document.habboLoggedIn = {{ session.loggedIn }};
        var habboReqPath = "{{ site.sitePath }}";
        var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery/";
        var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
        var habboPartner = "";
        var habboDefaultClientPopupUrl = "/client";

        
        window.name = "82d01adba997d04a81f688ed8a10e54b";
        if (typeof HabboClient != "undefined") {
            HabboClient.windowName = "82d01adba997d04a81f688ed8a10e54b";
            HabboClient.maximizeWindow = true;
        }
    </script>
    <link href='//fonts.googleapis.com/css?family=Ubuntu:400,700,400italic,700italic|Ubuntu+Medium' rel='stylesheet' type='text/css'>
    <noscript>
        <meta http-equiv="refresh" content="0;url=/client/nojs" />
    </noscript>
    <link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/habboflashclient.css" type="text/css" />
    <script src="{{ site.staticContentPath }}/web-gallery/static/js/habboflashclient.js" type="text/javascript"></script>
    <script type="text/javascript">
        FlashExternalInterface.loginLogEnabled = true;

        FlashExternalInterface.logLoginStep("web.view.start");

        if (top == self) {
            FlashHabboClient.cacheCheck();
        }
        var flashvars = {
            "client.allow.cross.domain": "1",
            "client.notify.cross.domain": "0",
            "connection.info.host": "{{ site.loaderGameIp }}",
            "connection.info.port": "12323",
            "site.url": "{{ site.sitePath }}/",
            "url.prefix": "{{ site.sitePath }}/",
            "client.reload.url": "/disconnected",
            "client.fatal.error.url": "{{ site.sitePath }}/disconnected",
            "client.connection.failed.url": "{{ site.sitePath }}/disconnected",
            "external.variables.txt": "{{ site.loaderFlashVariables }}",
            "external.texts.txt": "{{ site.loaderFlashTexts }}",
            "use.sso.ticket": "1",
            "sso.ticket": "{{ ssoTicket }}",
            "processlog.enabled": "1",
            "account_id": "1",
            "client.starting": "Hotel is loading, please wait.",
            "flash.client.url": "{{ site.loaderFlashBase }}",
            "user.hash": "ticket",
            "has.identity": "0",
            "flash.client.origin": "popup",
            "country_code": "US"{% if forwardRoom %},
			"forward.type": "{{ forwardType }}",
			"forward.id": "{{ forwardId}}"{% endif %}
        };
        var params = {
            "base": "{{ site.loaderFlashBase }}",
            "allowScriptAccess": "always",
            "menu": "false"
        };

        params["wmode"] = "opaque";

        FlashExternalInterface.signoutUrl = "/disconnected";

        var clientUrl = "{{ site.loaderFlashSwf }}";
        swfobject.embedSWF(clientUrl, "flash-container", "100%", "100%", "11.1.0", "/client/expressInstall.swf", flashvars, params, null, FlashExternalInterface.embedSwfCallback);

        window.onbeforeunload = unloading;

        function unloading() {
            var clientObject;
            if (navigator.appName.indexOf("Microsoft") != -1) {
                clientObject = window["flash-container"];
            } else {
                clientObject = document["flash-container"];
            }
            try {
                clientObject.unloading();
            } catch (e) {}
        }
        window.onresize = function() {
            HabboClient.storeWindowSize();
        }.debounce(0.5);
    </script>

    <!--[if IE 8]>
		<link rel="stylesheet" href="/client/styles/css/ie8.css" type="text/css" />
	<![endif]-->
    <!--[if lt IE 8]>
		<link rel="stylesheet" href="/client/styles/css/ie.css" type="text/css" />
	<![endif]-->
    <!--[if lt IE 7]>
		<link rel="stylesheet" href="/client/styles/css/ie6.css" type="text/css" />
		<script src="/client/js/pngfix.js" type="text/javascript"></script>
		<script type="text/javascript">
			try { document.execCommand('BackgroundImageCache', false, true); } catch(e) {}
		</script>
	<![endif]-->
</head>

<body id="client" class="flashclient">
    <div id="overlay"></div>
    <img src="/client/page_loader.gif" style="position:absolute; margin: -1500px;" />
    <div id="client-ui">
        <div id="flash-wrapper">
            <div id="flash-container">
                <div id="content" style="width: 400px; margin: 20px auto 0 auto;">
                    <div class="cbb clearfix">
                        <h2 class="title">Please update your Flash Player to the latest version.</h2>
						<div class="box-content">
							<p>You can install and download Adobe Flash Player here: <a href="http://get.adobe.com/flashplayer/">Install flash player</a>. More instructions for installation can be found here: <a href="http://www.adobe.com/products/flashplayer/productinfo/instructions/">More information</a></p>
							<p><a href="http://www.adobe.com/go/getflashplayer"><img src="http://www.adobe.com/images/shared/download_buttons/get_flash_player.gif" alt="Get Adobe Flash player" /></a></p>
						</div>
                    </div>
                </div>
                <noscript>
					<div style="width: 400px; margin: 20px auto 0 auto; text-align: center">
						<p>If you are not automatically redirected, please <a href="/client/nojs">click here</a></p>
					</div>
                </noscript>
            </div>
        </div>
        <div id="content" class="client-content"></div>
        <iframe id="game-content" class="hidden" allowtransparency="true" frameBorder="0" src="about:blank"></iframe>
        <iframe id="page-content" class="hidden" allowtransparency="true" frameBorder="0" src="about:blank"></iframe>
        <script type="text/javascript">
            RightClick.init("flash-wrapper", "flash-container");
            if (window.opener && window.opener != window && window.opener.location.href == "/") {
                window.opener.location.replace("/me");
            }
            $(document.body).addClassName("js");
            HabboClient.startPingListener();
            Pinger.start(true);
            HabboClient.resizeToFitScreenIfNeeded();
        </script>
        <script type="text/javascript">
            HabboView.run();
        </script>
</body>
</html>
