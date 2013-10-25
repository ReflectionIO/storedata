## 

library(truncreg)

## First, select apps with both a top rank and and grossing position

paid.raw$usesiap = as.numeric(! is.na(paid.raw$usesiap))

basic.indx <- !is.na(paid.raw$top.position) & !is.na(paid.raw$grossing.position)
basic.df <- paid.raw[basic.indx,]

basic.model <- truncreg(log(grossing.position) ~ log(top.position)+log(price),data=basic.df,point=cut.point,direction="right")

## compute basic coefficients

ag <- -1/basic.model$coefficients[c("log(price)")]
ap <- -1* basic.model$coefficients[c("log(top.position)")]/basic.model$coefficients[c("log(price)")]
b.ratio <- exp(-1*basic.model$coefficients[c("(Intercept)")]/basic.model$coefficients[c("log(price)")])

## simple estimate of coefficient of determination

r2 <- cor(log(basic.df$grossing.position),basic.model$fitted.values)^2


## Output data and parameters for simulation

