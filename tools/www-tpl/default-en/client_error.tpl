
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Error </title>

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
document.habboLoggedIn = true;
var habboName = "Alex";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.sitePath }}/habbo-imaging/";
var habboPartner = "";
window.name = "client";
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
<meta name="build" content="PHPRetro 4.0.8 " />
</head>
<body id="popup" class="process-template client_error">
<div id="container">
    <div id="content">

	    <div id="process-content" class="centered-client-error">
	       	<div id="column1" class="column">

				<div class="habblet-container ">		
						<div class="cbb clearfix orange ">
	
							<h2 class="title">Oops!!							</h2>
						<div class="box-content">
    <div class="info-client_error-text">
       <p>Oops, the client encountered a technical problem. Not to worry this error has now been recorded to our system and will be investigated by our support team.</p>

       <p>Please re-open <a onclick="openOrFocusHabbo(this); return false;" target="client" href="{{ site.sitePath }}/client">hotel</a> to continue. We are sorry for the inconvenience.</p>
    </div>
    <div class="retry-enter-hotel">
    <div class="hotel-open">
        <a id="enter-hotel-open-image" class="open" href="{{ site.sitePath }}/client" target="client" onclick="HabboClient.openOrFocus(this); return false;">
        <div class="hotel-open-image-splash"></div>
        <div class="hotel-image hotel-open-image"></div>

        </a>
        <div class="hotel-open-button-content">
           <a class="open" href="{{ site.sitePath }}/client" target="client" onclick="HabboClient.openOrFocus(this); return false;">Enter</a>
            <span class="open"></span>
        </div>
    </div>
    </div>
<script type="text/javascript">
document.observe("dom:loaded", function() {
    var titles = $$("h2.title");
    if (titles.length > 0) { {% autoescape 'html' %}
        Element.insert(titles[0], "(#{{ errorId }}) ");
    }{% endautoescape %}
});
</script>

</div>


<script type="text/javascript">
  document.observe("dom:loaded", function() {
    ClientMessageHandler.googleEvent("client_error", "unknown");
  });
</script>
	
						
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
			 

</div>
<script type="text/javascript">
HabboView.run();
</script>
<div id="column2" class="column">
</div>
<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->
		</div>
    </div>
</div>


</body>
</html>