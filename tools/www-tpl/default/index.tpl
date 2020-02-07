<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"> 
<html xmlns="http://www.w3.org/1999/xhtml"> 
	<head> 
		<title>Layout</title>
		<link rel="stylesheet" type="text/css" href="./style/global.css" />
		<link rel="stylesheet" type="text/css" href="./style/button.css" />
		<link rel="stylesheet" type="text/css" href="./style/index.css" />
		
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	</head>
	<body>
		<div class="center content round">
			<div class="head">
				<img src="./images/logo.png" alt="Logo" />
				<div class="online"><b>0</b> Habbo's online now!</div>
			</div>
			
			<div class="message red round">
				<p><b>Title</b><br />
				Sing-in error!</p>
			</div>
			
			<div class="left login small round">
				<p class="title">Login in to Habbo</p>
				
				<form action="./me.html" method="post">
					<p><label for="username">Username:</label><br />
					<input type="text" name="username" id="username" /></p>
					<p><label for="password">Password:</label><br />
					<input type="password" name="password" id="password" /></p>
					
					<input type="submit" class="button round" name="inlogform" value="Sign in" />
				</form>
			</div>
			
			<div class="right register green small round">
				New here?
				<p><img src="./images/smallhotel.png" alt="hotel" /></p>
				<a href="./register.html">REGISTER FOR FREE!</a>
			</div>
			
			<div class="clear"></div>
		</div>
		
		<div class="center copyright">
			&copy; PEjump & Joopie. 2011. All right reseverd.
		</div>
	</body>
</html>