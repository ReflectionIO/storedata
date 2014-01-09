
## pick up parameters and data

## Now, look at in-app purchases
## need to combine data from free and grossing into a data frame

free.indx  <- !is.na(free.raw$top.position) & !is.na(free.raw$grossing.position) & !is.na(free.raw$usesiap)
my.iap.df <- rbind(basic.df,free.raw[free.indx,])
my.iap.df$iap.ind <- as.numeric(my.iap.df$usesiap == 1 )

my.iap.df$iap.ind[is.na(my.iap.df$iap.ind)] <-  0


my.start <- list(b0=3,b1=0.4,b2=-0.3,th=0.2)

iap.model <- nls(log(grossing.position)~b0+b1*log(top.position)+b2*log(price+th*as.numeric(iap.ind)),data=my.iap.df,start=my.start)

iap.estimates <- summary(iap.model)$parameters[,1]
names(iap.estimates) <- row.names(summary(iap.model)$parameters)
iap.ag <- -1/iap.estimates["b2"]
iap.ap <- -1*iap.estimates["b1"]/iap.estimates["b2"]
th <- iap.estimates["th"]
## crude r^2

iap.r2 <- cor(log(my.iap.df$grossing.position),fitted.values(iap.model))^2


## Now, free apps

free.ind <- !is.na(free.raw$top.position) & !is.na(free.raw$grossing.position)
my.free.df <- free.raw[free.ind,]


free.model <- truncreg(log(grossing.position)~log(top.position),data=my.free.df,point=cut.point,direction="right") 


af <- free.model$coefficients["log(top.position)"]*ag



## note, using theta (th) computed from the previous stage, following discussion with William

bf <- exp(ag*free.model$coefficients["(Intercept)"])*bg/th

