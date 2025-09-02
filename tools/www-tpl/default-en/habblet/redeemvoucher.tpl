<ul>

    <li class="even icon-purse">
        <div>You Currently Have:</div>
        <span class="purse-balance-amount">{{ playerDetails.credits }} Coins</span>
        <div class="purse-tx"><a href="{{ site.sitePath }}/credits/history">Account transactions</a></div>
    </li>

    <li class="odd">
        <div class="box-content">

            <div>Enter voucher code (without spaces):</div>
            <input type="text" name="voucherCode" value="" id="purse-habblet-redeemcode-string" class="redeemcode" />
            <a href="#" id="purse-redeemcode-button" class="new-button purse-icon" style="float:left"><b><span></span>Enter</b><i></i></a>
        </div>
    </li>
</ul>
<ul>
<div id="purse-redeem-result">
				{% if voucherResult == 'error' %}
        <div class="redeem-error"> 
            <div class="rounded rounded-red"> 
                Your redeem code could not be found. Please try again.            
						</div> 
        </div>
				{% elseif voucherResult == 'too_new' %}
				<div class="redeem-error"> 
            <div class="rounded rounded-red"> 
                Sorry, your account is too new and cannot redeeem this voucher.            
						</div> 
        </div>
				{% else %}
				<div class="redeem-success"> 
            <div class="rounded rounded-green"> 
                Voucher redemption success         
						</div> 
        </div>
				{% endif %}
</div>