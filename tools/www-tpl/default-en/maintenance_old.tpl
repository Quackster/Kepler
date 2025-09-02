
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="fr" lang="fr">
<head>
	<meta http-equiv="content-type" content="text/html" />
	<title>Habbo</title>
	
	<script type="text/javascript" src="https://retro.alex-dev.org/web-gallery/maintenance/new/js/jquery.min.js"></script>
	<script type="text/javascript" src="https://retro.alex-dev.org/web-gallery/maintenance/new/js/jquery.tweet.js"></script>
	
	<link href="https://retro.alex-dev.org/web-gallery/maintenance/new/style/maintenance.css" rel="stylesheet" type="text/css" />
	

</head>
<body>
<div id="container">
	<div id="content">
		<div id="header" class="clearfix">
			<h1><span></span></h1>
		</div>
		<div id="process-content">

<div class="fireman">

<h1>Maintenance break!</h1>

<p>
Sorry! Habbo is being worked on at the moment.</p>
<p>We'll be back soon. We promise.</p>
</div>

<div class="tweet-container">

<h2>What's going on?</h2>

<div class="tweet"></div>

</div>

{% include "base/footer.tpl" %}


<script type='text/javascript'>
$(document).ready(function(){
  $(".tweet").tweet({
    username: "phpretro",
    count: 5
  });
});
</script>

</body>
</html>