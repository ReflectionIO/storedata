

## Now,  simulate the process of being given download data
## This is a proxy for what would happen with real data
## Suppose Dt=input parameter


Dt.sim  <- Dt.in
bp.sim <- Dt.sim/(sum((1:cut.point)^(-ap)))
bg.sim <- b.ratio*bp.sim

## Sample some apps
## NOTE, that this is a proxy for what would happen with real data
## 
Napps.with.info <- Napps
sim.indx <- sample(1:nrow(basic.df),Napps.with.info,replace=F)

apps.with.info.df <- basic.df[sim.indx,]

downloads.info <- bg.sim*((apps.with.info.df$grossing.position)^(-ag))/apps.with.info.df$price


## This is the end of the proxy bit

## Now we have some app-data, we can infer Dt

my.labelled.df <- cbind(apps.with.info.df,downloads.info)

labelled.apps.model <- lm(log(downloads.info)~log(grossing.position),data=my.labelled.df)

## compute Dt
Dt <- trunc(sum(exp(predict(labelled.apps.model,data.frame(grossing.position=1:cut.point)))))

## compute remaining parameters

bp <- Dt/(sum((1:cut.point)^(-ap)))
bg <- b.ratio *bp
