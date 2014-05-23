# 1 - Include extra term
# 2 - Smoother functionality to accept developer data along the way
# 3 - Uses an updated Semi-Parametric Truncated regression

# cut point is the entire paid list positions
# Napps is the developer data
# Downloads is the total aggregated in a day

#cut.point.par <- 400
#Napps  <- 40
#Dt.in <- 5000000

## Grossing data for Paid applications ONLY
#gross.data <- read.csv("PaidGrossingNA.csv", skip=1, as.is=T)

paid.raw$usesiap = as.numeric(! is.na(paid.raw$usesiap))

# Allow for truncated regression (standard package)
require(truncreg)

#require(MASS)

# Allow for Robust Regression
#require(censReg)


## First, select apps with both a top rank and and grossing position
basic.indx <- !is.na(paid.raw$top.position) & !is.na(paid.raw$grossing.position)
basic.df <- paid.raw[basic.indx,]

basic.model <- truncreg(log(grossing.position) ~ log(top.position) + log(price), data=basic.df, 
point=cut.point, direction="right")



## Compute basic coefficients as in Mellon paper
ag <- -1/basic.model$coefficients[c("log(price)")]

ap <- -1* basic.model$coefficients[c("log(top.position)")]/basic.model$coefficients[c("log(price)")]

b.ratio <- exp(-1*basic.model$coefficients[c("(Intercept)")]/basic.model$coefficients[c("log(price)")])



## Now, simulate the process of being given download data
## This is a proxy for what would happen with the real data
## Suppose Dt = input parameter

# This guess is vitally important - an aggregated number has to be accurate
Dt.sim  <- Dt.in

bp.sim <- Dt.sim/(sum((1:cut.point)^-ap))

bg.sim <- b.ratio*bp.sim



## Sample some apps
## NOTE - that this is a proxy for what would happen with real data
Napps.with.info <- Napps

sim.indx <- sample(1:nrow(basic.df), Napps.with.info, replace=F)

apps.with.info.df <- basic.df[sim.indx,]


# From Equation (3) in the Paper!
downloads.info <- bg.sim*((apps.with.info.df$grossing.position)^(-ag)) / apps.with.info.df$price



## This is the end of the proxy section - the developer data will be above rejigged though


## Now we have some app-data, we can infer Dt
my.labelled.df <- cbind(apps.with.info.df, downloads.info)

#This is a robust version of the previous regression
labelled.apps.model <- lm(log(downloads.info) ~ log(grossing.position), data = my.labelled.df)



## compute aggregated Downloads Dt                                                               
Dt <- trunc(sum(exp(predict(labelled.apps.model, data.frame(grossing.position=41:cut.point))))) + sum(downloads.info)

## Compute remaining parameters
#  From paper just before equation (9) - simple algebra

bp <- Dt/(sum((1:cut.point)^(-ap)))
bg <- b.ratio *bp




