{% include "base/email_header.tpl" %}
<p style="font-size: 18px;"><b>Welcome to {{ site.siteName}}, {{ playerName }}!</b></p>
<p>Thanks for registering at <a style="color: #66b3ff" href="{{ site.sitePath }}/?email">{{ site.emailHotelName }}</a>.</p>
<p><b><a style="color: #66b3ff" href="{{ site.sitePath }}/account/activate?id={{ playerId }}&code={{ activationCode }}">Please activate your account by clicking here</a>.</b></p>
<p>Here are your user details:</p>
<p style="margin-left: 30px;"><b>{{ site.siteName}} name:</b> {{ playerName }}</p>
<p style="margin-left: 30px;"><b>Email:</b> {{ playerEmail }}</p>
<p>Keep this information safe, you need your username and email to reset your password if you forget it.</br>
<hr>
<p>Here's some other stuff you may want to do:</p>
</ul>
	<li style="padding-left: 30px;"><a style="color: #66b3ff" href="{{ site.sitePath }}/profile?tab=2">Change account settings</a>.</li>
	<li style="padding-left: 30px;"><a style="color: #66b3ff" href="{{ site.sitePath }}/?emaildelete">Completely delete this email address from your user profile</a>.</li>
</ul>
{% include "base/email_footer.tpl" %}