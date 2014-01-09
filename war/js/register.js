$(document).ready(function() {
	/* Validation Form with AJAX on Submit */
	$('#submit').click(function(){
		$('span.error').fadeOut('slow');
		$('span.valid').fadeOut('slow');
		$('#thanks').hide();
		$('#error').hide();
		$('#timedout').hide();
		$('#state').hide();
		
		var error = false;
		
		var forename = $.trim($('#forename').val()); 
		if(forename == "") {
			$('#forename').after("<span class='error'></span>");
			$('#forename').parent().find('.error').fadeIn('slow');
			error = true; 
		} else {
			$('#forename').after("<span class='valid'></span>");
			$('#forename').parent().find('.valid').fadeIn('slow');			
		}
		
		var surname = $.trim($('#surname').val()); 
		if(surname == "") {
			$('#surname').after("<span class='error'></span>");
			$('#surname').parent().find('.error').fadeIn('slow');
			error = true; 
		} else {
			$('#surname').after("<span class='valid'></span>");
			$('#surname').parent().find('.valid').fadeIn('slow');			
		}
		
		var company = $.trim($('#company').val()); 
		if(company == "") {
			$('#company').after("<span class='error'></span>");
			$('#company').parent().find('.error').fadeIn('slow');
			error = true; 
		} else {
			$('#company').after("<span class='valid'></span>");
			$('#company').parent().find('.valid').fadeIn('slow');			
		}
		
		var checkEmail = /^([\w-\.]+@([\w-]+\.)+[\w-]{2,4})?$/; 
		var email = $('#email').val();
		if (email == "" || email == " ") {
			$('#email').after("<span class='error'></span>");
			$('#email').parent().find('.error').fadeIn('slow');
			error = true;
		} else if (!checkEmail.test(email)) { 
			$('#email').after("<span class='error'></span>");
			$('#email').parent().find('.error').fadeIn('slow');
			error = true;
		} else {
			$('#email').after("<span class='valid'></span>");
			$('#email').parent().find('.valid').fadeIn('slow');			
		}
		
		var action = $('#action').val();
		if (action != "register") {
			error = true;
		}
		
		if(error == true) {
			$('#error').fadeIn('slow');
			setTimeout(function() {
			    $('#error').fadeOut('slow');
			}, 3000);
			return false;
		}
		
		var data_string = $('#contact-form').serialize();
		
		$.ajax({
			type: "POST",
			url: "/sendmail",
			data: {forename:forename,surname:surname,company:company,email:email,action:action}, 
			timeout: 60000,
			error: function(request,error) {
				if (error == "timeout") {
					$('#timedout').fadeIn('slow');
					setTimeout(function() {
					    $('#timedout').fadeOut('slow');
					}, 3000);
				}
				else {
					$('#state').fadeIn('slow');
					$("#state").html('The following error occured: ' + error + '');
					setTimeout(function() {
					    $('#state').fadeOut('slow');
					}, 3000);
				}
			},
			success: function() {
				$('span.valid').remove();
				$('#thanks').fadeIn('slow');
				$('input').val('');
				$('textarea').val('');
				setTimeout(function() {
				    $('#thanks').fadeOut('slow');
				}, 3000);
			}
		});
		
		return false;
	});
});