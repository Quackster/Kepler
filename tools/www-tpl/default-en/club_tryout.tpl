
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Club Tryout </title>

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
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/styles/local/com.css" type="text/css" />

<script src="{{ site.staticContentPath }}/web-gallery/js/local/com.js" type="text/javascript"></script>

<script type="text/javascript">
document.habboLoggedIn = {{ session.loggedIn }};
var habboName = "{{ session.loggedIn ? playerDetails.getName() : "" }}";
var ad_keywords = "";
var habboReqPath = "{{ site.sitePath }}";
var habboStaticFilePath = "{{ site.staticContentPath }}/web-gallery";
var habboImagerUrl = "{{ site.staticContentPath }}/habbo-imaging/";
var habboPartner = "";
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

{% if session.loggedIn == false %}
<body id="home" class="anonymous ">
{% else %}
<body id="home" class=" ">
{% endif %}

{% include "base/header.tpl" %}

<div id="content-container">

<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
			<li class="">
				<a href="{{ site.sitePath }}/credits">Coins</a>			</li>
			<li class="">
				<a href="{{ site.sitePath }}/credits/club">{{ site.siteName }} Club</a>			</li>
			<li class="">
				<a href="{{ site.sitePath }}/credits/collectables">Collectables</a>			</li>
    		<li class=" last">
				<a href="{{ site.sitePath }}/credits/pixels">Pixels</a>    		</li>
		</ul>
    </div>
</div>
<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div id="column1" class="column">

				<div class="habblet-container ">		
						<div class="cbb clearfix red ">
	
							<h2 class="title">{{ site.siteName }} Club Test Wardrobe							</h2>
						<div id="habboclub-tryout" class="box-content">

    <div class="rounded rounded-lightbrown clearfix">
       <p class="habboclub-logo heading">Try on the club clothes for free here and then use the menu on the right to become a member and wear the clothes in the Hotel.<br /><br />If you just want to change your look without using club clothes or joining {{ site.siteName }} Club, please go back to your <a href="{{ site.sitePath }}/profile">Account Settings.</a></p>
    </div>

    <div id="flashcontent">
        You need to have a Flash player installed on your computer before being able to edit your {{ site.siteName }} character. You can download the player from here: <a target="_blank" href="http://www.adobe.com/go/getflashplayer" >http://www.adobe.com/go/getflashplayer</a>
    </div>    
</div>

<script type="text/javascript" language="JavaScript">
    var swfobj = new SWFObject("{{ site.sitePath }}/flash/HabboRegistration.swf", "habboreg", "435", "400", "8");
    swfobj.addParam("base", "{{ site.sitePath }}/flash/");
    swfobj.addParam("wmode", "opaque");
    swfobj.addParam("AllowScriptAccess", "always");
    swfobj.addVariable("figuredata_url", "{{ site.sitePath }}/xml/figuredata.xml");
    swfobj.addVariable("draworder_url", "{{ site.sitePath }}/xml/draworder.xml");
    swfobj.addVariable("localization_url", "{{ site.sitePath }}/xml/figure_editor.xml");
    swfobj.addVariable("habbos_url", "{{ site.sitePath }}/xml/promo_habbos_v2.xml");
    swfobj.addVariable("figure", "{{ ("figure" is present) ? figure : "" }}");
    swfobj.addVariable("gender", "{{ ("sex" is present) ? sex : ""  }}");
    swfobj.addVariable("showClubSelections", "1");
    if (deconcept.SWFObjectUtil.getPlayerVersion()["major"] >= 8) {
	    $("flashcontent").setStyle({ textAlign: "center", "marginTop" : "10px" });
	    swfobj.write("flashcontent");	    
    }
</script>			
					</div>
				</div>

				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

</div>
<div id="column2" class="column">
			     		
				<div class="habblet-container ">		
						<div class="cbb clearfix hcred ">
	
							<h2 class="title">My Membership							</h2>
							

						<script src="{{ site.staticContentPath }}/web-gallery/static/js/habboclub.js" type="text/javascript"></script>
						{% include "base/hc_status.tpl" %}
					</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

				<div class="habblet-container ">		
						<div class="cbb clearfix lightbrown ">
	
							<h2 class="title">What is {{ site.siteName }} Club?							</h2>

						<div id="habboclub-info" class="box-content">
    <p>{{ site.siteName }} Club is our VIP members-only club - absolutely no riff-raff admitted! Members enjoy a wide range of benefits, including exclusive clothes, free gifts and an extended Friends List. See below for all the sparkly, attractive reasons to join.</p>
    <h3 class="heading">1. Extra Clothes &amp; Accessories</h3>
    <h3 class="heading">2. Free Furni</h3>
    <h3 class="heading">3. Exclusive Room Layouts</h3>
    <h3 class="heading">4. Access All Areas</h3>

    <h3 class="heading">5. Homepage Upgrades</h3>
    <h3 class="heading">6. More Friends</h3>
    <h3 class="heading">7. Special Commands</h3>
    <p class="read-more"><a href="{{ site.sitePath }}/credits/club">Read more &raquo;</a></p>
</div>
	
						
					</div>
				</div>

				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>

</div>
<script type="text/javascript">
HabboView.run();

</script>


<div id="column3" class="column">
				<div class="habblet-container ">
						<div class="ad-container">
						{% include "base/ads_container.tpl" %}
						</div>
				</div>
				<script type="text/javascript">if (!$(document.body).hasClassName('process-template')) { Rounder.init(); }</script>
</div>

<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->
    </div>
{% include "base/footer.tpl" %}

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>