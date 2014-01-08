jQuery(document).ready(function() {
	/* Validation Form with AJAX on Submit */
	jQuery('#submit').click(function(){
		jQuery('span.error').fadeOut('slow');
		jQuery('span.valid').fadeOut('slow');
		jQuery('#thanks').hide();
		jQuery('#error').hide();
		jQuery('#timedout').hide();
		jQuery('#state').hide();
		
		var error = false;
		
		var name = jQuery('#name').val(); 
		if(name == "" || name == " ") {
			jQuery('#name').after("<span class='error'></span>");
			jQuery('#name').parent().find('.error').fadeIn('slow');
			error = true; 
		} else {
			jQuery('#name').after("<span class='valid'></span>");
			jQuery('#name').parent().find('.valid').fadeIn('slow');			
		}
		
		var checkEmail = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/; 
		var email = jQuery('#email').val();
		if (email == "" || email == " ") {
			jQuery('#email').after("<span class='error'></span>");
			jQuery('#email').parent().find('.error').fadeIn('slow');
			error = true;
		} else if (!checkEmail.test(email)) { 
			jQuery('#email').after("<span class='error'></span>");
			jQuery('#email').parent().find('.error').fadeIn('slow');
			error = true;
		} else {
			jQuery('#email').after("<span class='valid'></span>");
			jQuery('#email').parent().find('.valid').fadeIn('slow');			
		}
		
		var message = jQuery('#message').val(); 
		if(message == "" || message == " ") {
			jQuery('#message').after("<span class='error'></span>");
			jQuery('#message').parent().find('.error').fadeIn('slow');
			error = true; 
		} else {
			jQuery('#message').after("<span class='valid'></span>");
			jQuery('#message').parent().find('.valid').fadeIn('slow');			
		}
		
		var action = jQuery('#action').val();
		if (action != "contact") {
			error = true;
		}
		
		if(error == true) {
			jQuery('#error').fadeIn('slow');
			setTimeout(function() {
			    jQuery('#error').fadeOut('slow');
			}, 3000);
			return false;
		}
		
		var data_string = jQuery('#contact-form').serialize();
		
		jQuery.ajax({
			type: "POST",
			url: "/sendmail",
			data: {name:name,email:email,message:message,action:action}, 
			timeout: 60000,
			error: function(request,error) {
				if (error == "timeout") {
					jQuery('#timedout').fadeIn('slow');
					setTimeout(function() {
					    jQuery('#timedout').fadeOut('slow');
					}, 3000);
				}
				else {
					jQuery('#state').fadeIn('slow');
					jQuery("#state").html('The following error occured: ' + error + '');
					setTimeout(function() {
					    jQuery('#state').fadeOut('slow');
					}, 3000);
				}
			},
			success: function() {
				jQuery('span.valid').remove();
				jQuery('#thanks').fadeIn('slow');
				jQuery('input').val('');
				jQuery('textarea').val('');
				setTimeout(function() {
				    jQuery('#thanks').fadeOut('slow');
				}, 3000);
			}
		});
		
		return false;
	});
});