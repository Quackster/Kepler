{% if alert.hasAlert %}

<div class="alert alert-{{ alert.colour }}">
  {{ alert.message }}
</div>


{% endif %}