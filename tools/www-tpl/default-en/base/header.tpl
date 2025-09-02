{% if session.loggedIn == false %}

<div id="overlay"></div>

<div id="header-container">
	<div id="header" class="clearfix">
		<h1><a href="{{ site.sitePath }}/"></a></h1>
       <div id="subnavi">
            <div id="subnavi-user">
                <div class="clearfix">&nbsp;</div>
                <p>
				        <a href="{{ site.sitePath }}/client" id="enter-hotel-open-medium-link" target="client" onclick="HabboClient.openOrFocus(this); return false;">Enter {{ site.siteName }} Hotel</a>
                </p>
            </div>
            <div id="subnavi-login">
                <form action="{{ site.sitePath }}/account/submit" method="post" id="login-form">
            		<input type="hidden" name="page" value="/community" />
                    <ul>
                        <li>
                            <label for="login-username" class="login-text"><b>Username</b></label>

                            <input tabindex="1" type="text" class="login-field" name="username" id="login-username" />
		                    <a href="#" id="login-submit-new-button" class="new-button" style="float: left; display:none"><b>Log in</b><i></i></a>
                            <input type="submit" id="login-submit-button" value="Log in" class="submit"/>
                        </li>
                        <li>
                            <label for="login-password" class="login-text"><b>Password</b></label>
                            <input tabindex="2" type="password" class="login-field" name="password" id="login-password" />
                            <input tabindex="3" type="checkbox" name="_login_remember_me" value="true" id="login-remember-me" />

                            <label for="login-remember-me" class="left">Remember me</label>
                        </li>
                    </ul>
                </form>
                <div id="subnavi-login-help" class="clearfix">
                    <ul>
                        <li class="register"><a href="{{ site.sitePath }}/account/password/forgot" id="forgot-password"><span>I forgot my password/username</span></a></li>
                    	<li><a href="{{ site.sitePath }}/register"><span>Register</span></a></li>

                    </ul>
                </div>
<div id="remember-me-notification" class="bottom-bubble" style="display:none;">
	<div class="bottom-bubble-t"><div></div></div>
	<div class="bottom-bubble-c">
					By selecting 'remember me' you will stay signed in on this computer until you click 'Sign Out'. If this is a public computer please do not use this feature.	</div>
	<div class="bottom-bubble-b"><div></div></div>
</div>

            </div>
        </div>
		<script type="text/javascript">
			LoginFormUI.init();
			RememberMeUI.init("right");
		</script> 
		<ul id="navi">
			<li id="tab-register-now"><a href="{{ site.sitePath }}/register">Register now!</a><span></span></li>
			
			{% if session.currentPage == "community" %}
			<li class="selected">
				<strong>Community</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/community">Community</a>			<span></span>
			</li>
			{% endif %}
			
			{% if session.currentPage == "credits" %}
			<li class="selected">
				<strong>Coins</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/credits">Coins</a>			<span></span>
			</li>
			{% endif %}
			
			{% if session.currentPage == "games" %}
			<li class="selected">
				<strong>Games</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/games">Games</a>			<span></span>
			</li>
			{% endif %}
			
		</ul>

        <div id="habbos-online"><div class="rounded"><span>{{ site.formattedUsersOnline }} {{ site.siteName }}s online</span></div></div>
	</div>
</div>

{% else %}

<div id="overlay"></div>
<div id="header-container">
	<div id="header" class="clearfix">
		<h1><a href="{{ site.sitePath }}/"></a></h1>
       <div id="subnavi">
			<div id="subnavi-user">
				<ul>
					<li id="myfriends"><a href="#"><span>My Friends</span></a><span class="r"></span></li>
					<li id="mygroups"><a href="#"><span>My Groups</span></a><span class="r"></span></li>
					<li id="myrooms"><a href="#"><span>My Rooms</span></a><span class="r"></span></li>
				</ul>
				
				 {% if site.serverOnline %}
				<a href="client.php" id="enter-hotel-open-medium-link" target="client" onclick="openOrFocusHabbo('client'); return false;">Enter {{ site.siteName }}</a>
				                        {% else %}
                        <div id="hotel-closed-medium">{{ site.siteName }} is offline</div> 
                        {% endif %}
			</div>
								
												


            <div id="subnavi-search">
                <div id="subnavi-search-upper">
                <ul id="subnavi-search-links">
                    <li><a href="./iot/go" target="habbohelp" onclick="openOrFocusHelp(this); return false">Help</a></li>
					<li><a href="{{ site.sitePath }}/account/logout" class="userlink">Sign Out</a></li>
				</ul>
                </div>
                <form name="tag_search_form" action="./home" class="search-box clearfix">
					<a id="search-button" class="new-button search-icon" href="#" onclick="$('search-button').up('form').submit(); return false;"><b><span></span></b><i></i></a>					
					<input type="text" name="tag" id="search_query" value="User Profile.." class="search-box-query search-box-onfocus" style="float: right"/>
				</form>
                <script type="text/javascript">SearchBoxHelper.init();</script>
            </div>
        </div>
        <script type="text/javascript">
		L10N.put("purchase.group.title", "Create a group");

		document.observe("dom:loaded", function() {
            $("signout").observe("click", function() {
                HabboClient.close();
            });
        });
        </script>
		<ul id="navi">
			{% if session.currentPage == "me" %}
			<li class="selected">
				<strong>{{ playerDetails.getName() }} </strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/me">{{ playerDetails.getName() }}</a>			<span></span>
			</li>
			{% endif %}
			
			{% if session.currentPage == "community" %}
			<li class="selected">
				<strong>Community</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/community">Community</a>			<span></span>
			</li>
			{% endif %}
			
			{% if session.currentPage == "credits" %}
			<li class="selected">
				<strong>Coins</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/credits">Coins</a>			<span></span>
			</li>
			{% endif %}
			
			{% if session.currentPage == "games" %}
			<li class="selected">
				<strong>Games</strong>			<span></span>
			</li>
			{% else %}
			<li>
				<a href="{{ site.sitePath }}/games">Games</a>			<span></span>
			</li>
			{% endif %}
			{% if playerDetails.getRank().getRankId() >= 6 %}
				<li id="tab-register-now"><a href="{{ site.sitePath }}/allseeingeye/hk/">Housekeeping</a><span></span></li>
			{% endif %}
			
		</ul>

        <div id="habbos-online"><div class="rounded"><span>{{ site.formattedUsersOnline }} {{ site.siteName }}s online</span></div></div>
	</div>
</div>

{% endif %}
