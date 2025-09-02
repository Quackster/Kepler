					{% if newbieNextGift < 3 %}
					<div class="gift-img">
					  {% if newbieNextGift == 1 %}
                          <img src="{{ site.staticContentPath }}/web-gallery/v2/images/welcome/newbie_furni/noob_stool_{{ newbieRoomLayout }}.png" alt="My first starter stool">
					  {% elseif newbieNextGift == 2 %}
						  <img src="{{ site.staticContentPath }}/web-gallery/v2/images/welcome/newbie_furni/noob_plant.png" alt="My first plant">
					  {% endif %}
                    </div>
                    <div class="gift-content-container">
                      <p class="gift-content">
					  {% if newbieNextGift == 1 %}
                        Your next piece of free furniture will be <strong>STARTER STOOL</strong>
					  {% elseif newbieNextGift == 2 %}
						Your next piece of free furniture will be <strong>LUCKY BAMBOO PLANT</strong>
					  {% endif %}
					  </p>
                      <p>
                        <b>Time left:</b>
                        <span id="gift-countdown"></span>
                      </p>
                      <p class="last">
                        <a class="new-button green-button" href="{{ site.sitePath }}/client?forwardId=2&roomId={{ playerDetails.getSelectedRoomId() }}" target="client" onclick="HabboClient.roomForward(this, '{{ playerDetails.getSelectedRoomId() }}', 'private'); return false;"><b>Go to your room &gt;&gt;</b><i></i></a>
                      </p>
                      <br style="clear: both" />
                    </div>
                    <script type="text/javascript">
                      L10N.put('time.hours', '{0}h');
                      L10N.put('time.minutes', '{0}min');
                      L10N.put('time.seconds', '{0}s');
                      GiftQueueHabblet.init({{ newbieGiftSeconds }});
                    </script>
					{% else %}
					<p>
                      How do you get more furniture into Your room?
                    </p>
                    <p>
                      You could buy a set of furniture for just 3 credits including a lamp, mat, and two armchairs. How do you do that?
                    </p>
                    <ul>
                      <li>1. Buy some credits from the <a href="{{ site.sitePath }}/credits">credits</a> section</li>
                      <li>2. Open the catalogue from the Hotel toolbar (Chair icon)</li>
                      <li>3. Open the deals section</li>
                      <li>4. Pick up the furni set You want</li>
                      <li>5. Thank You for shopping!</li>
                    </ul>
                    <p class="aftergift-img">
                      <img src="{{ site.staticContentPath }}/web-gallery/v2/images/giftqueue/aftergifts.png" alt="" width="289" height="63" />
                    </p>
                    <p class="last">
                      <a class="new-button green-button" href="{{ site.sitePath }}/client?forwardId=2&roomId={{ playerDetails.getSelectedRoomId() }}" target="client" onclick="HabboClient.roomForward(this, '{{ playerDetails.getSelectedRoomId() }}', 'private'); return false;"><b>Go to your room &gt;&gt;</b><i></i></a>
                    </p>
                    <script type="text/javascript">					  
                      HabboView.add(GiftQueueHabblet.initClosableHabblet);
                    </script>
					{% endif %}