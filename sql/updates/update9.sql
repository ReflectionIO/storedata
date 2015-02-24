delimiter $$

USE `rio`$$

INSERT INTO `event` (`type`, `code`, `name`, `description`, `priority`,`longbody`, `subject`,`shortbody`) VALUES 
('user','SCE','Sales gather: wrong credentials error','Send to data account owners to inform of the failed sales gather due to wrong credentials','high','Hi ${user.forename},\n\nYour sales report for the account ${dataaccount.username} has failed due to having the wrong username and/or password.\nPlease update your account credentials at www.reflection.io/#!users/linkedaccounts/${user.id}.\n\nThanks, The Reflection Team.','Wrong account credentials','-'),
('system','SGE','Sales gather: generic error', 'Send to admin to inform of the failed sales gather due to a generic error','high','The data account ${dataaccount.username} - id: ${dataaccount.id} gather failed on ${dataaccountfetch.date} with this message:\n${dataaccountfetch.data}.','Data account ${dataaccount.username} sales gather error','-')$$
