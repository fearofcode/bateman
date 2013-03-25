runtime <- "201303242306"

library(chron)
library(zoo)
png(paste("AAPL_plot_", runtime, ".png", sep=""), width=5000, height=1024) 
prices <- read.table(paste("AAPL_series_", runtime, ".csv", sep=""), header=T, sep=",")
trades <- read.table(paste("AAPL_trades_", runtime, ".csv", sep=""), header=T, sep=",")
zooPrices <- read.zoo(paste("AAPL_series_", runtime, ".csv", sep=""), header = TRUE, sep = ",", FUN=as.chron)
indexes = 1:nrow(prices)
prices$Index <- indexes

library(zoo) 
coreZooPrices <- zoo(coredata(zooPrices)) 
plot(prices$Index, prices$Price, type="l", xlab="Date", ylab="Price", xaxt="n")
ind <- seq(1, length(zooPrices), by=240)
axis(1, time(coreZooPrices)[ind], lab = format(time(zooPrices))[ind], tcl = 0.3)

rectColors = ifelse(trades$Profit > 0, rgb(0,1,0,0.2), rgb(1,0,0,0.2))

rect(xleft=trades$OpenIndex, xright=trades$CloseIndex, ytop=10000000, ybottom=-10000000, col=rectColors, border=NA)

texty = min(prices$Price) + (max(prices$Price)-min(prices$Price))/2
tradeLabels = sprintf("%s - %s, %.2f - %.2f: $%.2f", trades$Open, trades$Close, trades$OpenPrice, trades$ClosePrice, trades$Profit);
text(trades$OpenIndex + (trades$CloseIndex-trades$OpenIndex)/2, texty, labels=tradeLabels, srt=90, col='blue')

dev.off()
