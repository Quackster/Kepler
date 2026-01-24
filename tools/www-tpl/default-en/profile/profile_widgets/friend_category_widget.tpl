{% autoescape 'html' %}
							<div id="category-list">
								<div id="friends-category-title">
								Friend categories
								</div>
								<div class="category-default category-item selected-category" id="category-item-0">Friends</div>
								{% for category in categories %}
								<div id="category-item-{{ category.getId() }}" class="category-item ">
										<div class="category-name" id="category-{{ category.getId() }}">
												<span class="open-category" id="category-name-{{ category.getId() }}">{{ category.getName() }}</span>
												<span id="category-field-{{ category.getId() }}" style="display:none"><input class="edit-category-name" maxlength="32" id="category-input-{{ category.getId() }}" type="text" value="{{ category.getName() }}"/></span>
										</div>
										<div id="category-button-delete-{{ category.getId() }}" class="friendmanagement-small-icons friendmanagement-remove delete-category-tip"></div>
										<div id="category-button-edit-{{ category.getId() }}" class="friendmanagement-small-icons edit-category"></div>

										<div id="category-button-cancel-{{ category.getId() }}" style="display:none" class="friendmanagement-small-icons friendmanagement-remove cancel-edit-category"></div>
										<div id="category-button-save-{{ category.getId() }}" style="display:none" class="friendmanagement-small-icons friendmanagement-save save-category"></div>
								</div>
								{% endfor %}
								<input type="text" maxlength="32" id="category-name" class="create-category" />
								<div id="add-category-button" class="friendmanagement-small-icons add-category-item add-category"></div>
							</div>
						</div>
{% endautoescape %}