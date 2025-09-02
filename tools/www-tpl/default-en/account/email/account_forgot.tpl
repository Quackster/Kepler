
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: Forgotten password </title>

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


<meta name="description" content="Join the world's largest virtual hangout where you can meet and make friends. Design your own rooms, collect cool furniture, throw parties and so much more! Create your FREE Habbo today!" />
<meta name="keywords" content="Habbo, virtual, world, join, groups, forums, play, games, online, friends, teens, collecting, social network, create, collect, connect, furniture, virtual, goods, sharing, badges, social, networking, hangout, safe, music, celebrity, celebrity visits, cele" />

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
<body  class="process-template">

<div id="overlay"></div>

<div id="container">
	<div class="cbb process-template-box clearfix">
		<div id="content">
			{% include "../../base/frontpage_header.tpl" %}
			<div id="process-content">
<style type="text/css">
		div.left-column { float: left; width: 50% }
		div.right-column { float: right; width: 49% }
		label { display: block }
		input { width: 98% }
		input.process-button { width: auto; float: right }
	</style>

			<div id="process-content">
	        	<div class="left-column">
<div class="cbb clearfix">
    <h2 class="title">Forgotten Your Password?</h2>
    <div class="box-content">
		{% if invalidForgetPassword %}
		<div class="rounded rounded-red">
                Invalid username or e-mail address <br />
        </div>
        <div class="clear"></div>
		{% endif %}
		{% if validForgetPassword %}
		<div class="rounded rounded-green">
                An email has been sent with recovery details <br />
        </div>
        <div class="clear"></div>
		{% endif %}
        <p>Don't panic! Please enter your account information below and we'll send you an email telling you how to reset your password.</p>

        <div class="clear"></div>

        <form method="post" action="forgot" id="forgottenpw-form">
            <p>
            <label for="forgottenpw-username">Username</label>
            <input type="text" name="forgottenpw-username" id="forgottenpw-username" value="" />
            </p>

            <p>
            <label for="forgottenpw-email">Email address</label>
            <input type="text" name="forgottenpw-email" id="forgottenpw-email" value="" />
            </p>

            <p>
            <input type="submit" value="Request password email" name="actionForgot" class="submit process-button" id="forgottenpw-submit" />
            </p>
            <input type="hidden" value="default" name="origin" />
        </form>
    </div>
</div>

</div>


<div class="right-column">

<div class="cbb clearfix">
    <h2 class="title">Forgotten Your Habbo Name?</h2>
    <div class="box-content">
		{% if invalidForgetName %}
		<div class="rounded rounded-red">
                Invalid username or e-mail address <br />
        </div>
        <div class="clear"></div>
		{% endif %}
		{% if validForgetName %}
		<div class="rounded rounded-green">
                A list of names have been sent to the e-mail address <br />
        </div>
        <div class="clear"></div>
		{% endif %}
        <p>No problem - just enter your email address below and we'll send you a list of your accounts.</p>

        <div class="clear"></div>

        <form method="post" action="forgot" id="accountlist-form">
            <p>

            <label for="accountlist-owner-email">Email address</label>
            <input type="text" name="ownerEmailAddress" id="accountlist-owner-email" value="" />
            </p>

            <p>
            <input type="submit" value="Get my accounts" name="actionList" class="submit process-button" id="accountlist-submit" />
            </p>
            <input type="hidden" value="default" name="origin" />
        </form>

    </div>
</div>

<div class="cbb clearfix">
    <h2 class="title">False Alarm!</h2>
    <div class="box-content">
        <p>If you have remembered your password, or if you just came here by accident, click the link below to return to the homepage.</p>
        <p><a href="{{ site.sitePath }}">Back to homepage &raquo;</a></p>
    </div>
</div>

</div>


<!--[if lt IE 7]>
<script type="text/javascript">
Pngfix.doPngImageFix();
</script>
<![endif]-->

{% include "../../base/footer.tpl" %}

<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>