delimiter $$

USE `rio`$$

INSERT INTO `event` (`type`, `code`, `name`, `description`, `priority`,`longbody`, `subject`,`shortbody`) VALUES 
('user','SCE','Sales gather: wrong credentials error','Send to data account owners to inform of the failed sales gather due to wrong credentials','high','Hi ${user.forename},\n\nYour sales data gather for the account ${dataaccount.username} is failed due to wrong username and/or password.\nPlease check your account credentials on www.reflection.io/#!users/linkedaccounts/${user.id}.\n\nThanks, The Reflection Team.','Wrong account credentials','-'),
('user','SGE','Sales gather: generic error', 'Send to admin to inform of the failed sales gather due to a generic error','high','The data account ${dataaccount.username} - id: ${dataaccount.id} gather is failed on ${dataaccountfetch.date} with this message:\n${dataaccountfetch.data}.','Data account ${dataaccount.username} sales gather error','-')$$