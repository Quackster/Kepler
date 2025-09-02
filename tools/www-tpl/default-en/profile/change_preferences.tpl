
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>{{ site.siteName }}: My preferences </title>

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

<script src="{{ site.staticContentPath }}/web-gallery/static/js/settings.js" type="text/javascript"></script>
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/settings.css" type="text/css" />
<link rel="stylesheet" href="{{ site.staticContentPath }}/web-gallery/v2/styles/friendmanagement.css" type="text/css" />


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

{% include "../base/header.tpl" %}

<div id="content-container">

	<div id="navi2-container" class="pngbg">
    <div id="navi2" class="pngbg clearfix">
		<ul>
			<li class="">
				<a href="{{ site.sitePath }}/me">Home</a>			</li>
    		<li class="">
				<a href="{{ site.sitePath }}/home/{{ playerDetails.getName() }}">My Page</a>    		</li>
    		<li class="selected">
				Account Settings    		</li>
			<li class=" last">
				<a href="{{ site.sitePath }}/club">{{ site.siteName }} Club</a>
			</li>
		</ul>
    </div>
</div>
	
<div id="container">
	<div id="content" style="position: relative" class="clearfix">
    <div>
<div class="content">
<div class="habblet-container" style="float:left; width:210px;">
<div class="cbb settings">

<h2 class="title">Account Settings</h2>
<div class="box-content">
            <div id="settingsNavigation">
            <ul>
				<li><a href="{{ site.sitePath }}/profile?tab=1">My Clothing</a>
                </li><li class="selected">My Preferences
				{% if accountActivated %}
                </li><li><a href="{{ site.sitePath }}/profile?tab=3">My Email</a>
				{% else %}
				</li><li><a href="{{ site.sitePath }}/profile/verify">Email Changing & Verification</a>
				{% endif %}
                </li><li><a href="{{ site.sitePath }}/profile?tab=4">My Password</a>
                </li><li><a href="{{ site.sitePath }}/profile?tab=5">Friend Management</a>
								</li><li><a href="{{ site.sitePath }}/profile?tab=6">Trade Settings</a>
                </li>            </ul>
            </div>
</div></div>
    {% include "profile/profile_widgets/join_club.tpl" %}
</div>
    <div class="habblet-container " style="float:left; width: 560px;">
        <div class="cbb clearfix settings">

            <h2 class="title">Change your profile</h2>
            <div class="box-content">

{% if settingsSavedAlert %}
<div class="rounded rounded-green">Account settings updated successfully.<br />	</div><br />
{% endif %}

<form action="{{ site.sitePath }}/profile/profileupdate" method="post" id="profileForm">
<input type="hidden" name="tab" value="2" />
<input type="hidden" name="__app_key" value="HavanaWeb" />


<h3>Your motto</h3>

<p>
Your motto is what other {{ site.siteName }}s will see on your {{ site.siteName }} Home page and beneath your {{ site.siteName }} in the Hotel.</p>
{% autoescape 'html' %}
<p>
<span class="label">Motto:</span>
<input type="text" name="motto" size="32" maxlength="32" value="{{ playerDetails.motto }}" id="avatarmotto" />
</p>
{% endautoescape %}

<h3>Client preference</h3>

<p>
The plugin to use when clicking the go to hotel buttons (go to room, etc)<br />
<label><input type="radio" name="clientpreference" value="SHOCKWAVE" {{ SHOCKWAVEenabled }} />Shockwave</label>

<label><input type="radio" name="clientpreference" value="FLASH" {{ FLASHenabled }} />Flash</label>
</p>

<h3>Your page</h3>

<p>
Who can view your homepage:<br />
<label><input type="radio" name="visibility" value="EVERYONE" {{ profileVisibleEnabled }} />Visible to everyone</label>

<label><input type="radio" name="visibility" value="NOBODY" {{ profileVisibleDisabled }} />Invisible to everyone</label>
</p>

<!-- <h3>Email alerts</h3>

<p>
<label><input type="checkbox" name="emailMiniMailAlertEnabled" value="true" checked="checked" />Receive notifications on Minimail messages.</label> <br />
<label><input type="checkbox" name="emailFriendRequestAlertEnabled" value="true" checked="checked" />Receive notifications on friend requests.</label>
</p>
-->
<h3>Word Filter</h3>
<p>
<label><input type="checkbox" name="wordFilterSetting" value="false" {{ wordFilterSetting }}> Turn bad language filter off.</label>
</p>

<h3>Friend Requests</h3>
<p>
<label><input type="checkbox" name="allowFriendRequests" value="true" {{ allowFriendRequests }}> Friend requests enabled</label>
</p>

<h3>Friend Follow</h3>
<p>
Ability for other people to follow users between rooms:<br />
<label><input type="radio" name="followFriendSetting" value="true" {{ followFriendEnabled }} />Friends</label>
<label><input type="radio" name="followFriendSetting" value="false" {{ followFriendDisabled }} />Nobody</label>
</p>

<h3>Online Status</h3>
<p>
Select who can see your online status:<br />
<label><input type="radio" name="showOnlineStatus" value="true" {{ onlineStatusEnabled }} />Everybody</label>
<label><input type="radio" name="showOnlineStatus" value="false" {{ onlineStatusDisabled }} />Nobody</label>
</p>

<div class="settings-buttons">
<a href="#" class="new-button" style="display: none" id="profileForm-submit"><b>Save changes</b><i></i></a>
<noscript><input type="submit" value="Save changes" name="save" class="submit" /></noscript>
</div>

</form>

<script type="text/javascript">
$("profileForm-submit").observe("click", function(e) { e.stop(); $("profileForm").submit(); });
$("profileForm-submit").show();

$("profileForm-genToken").observe("click", function(e) { e.stop();
	new Ajax.Request(habboReqPath + "/habblet/ajax/token_generate", {
        onComplete: function(response) {
            document.getElementById('authenticationToken').value = response.responseText;
		}
    });
 });
$("profileForm-genToken").show();

</script>

</div>
</div>
</div>
</div>
</div>

<script type="text/javascript">
HabboView.run();

</script>


<div id="column3" class="column">
				<div class="habblet-container ">
						<div class="ad-container">
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

{% include "../base/footer.tpl" %}
	
<script type="text/javascript">
HabboView.run();
</script>


</body>
</html>