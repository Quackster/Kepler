{% include "base/email_header.tpl" %}
<p style="font-size: 18px;"><b>Account Recovery</b></p>
<p>Hello {{ playerName }},</a></p>
<p><b><a style="color: #66b3ff" href="{{ site.sitePath }}/account/password/recovery?id={{ playerId }}&code={{ recoveryCode }}">Please recover your account by clicking here</a>.</b></p>
<p>This link can only be used once and will lead you to a page to set your password. It expires after one day and nothing will happen if it's not used.</p>
<hr>
<p>If you did not request an account recovery, you can delete this email.</p>
{% include "base/email_footer.tpl" %}