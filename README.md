Bateman
-------

![Everything failed to subdue me. Soon everything seemed dull: another sunrise, the lives of heroes, falling in love, war, the discoveries people made about each other.](http://media.tumblr.com/tumblr_lmvx4uwp0h1qeqv7k.gif)

Bateman is an in-progress trading system meant to screen a subset of the US equities markets and tests out how well a simple [long](http://www.investopedia.com/terms/l/long.asp)-only trading strategy described below will work.

It's based off the observation that many symbols display sufficient daily volatility that their high will generally be significantly above their open, regardless of their price at the close of trading.

The strategy has its parameters refined by [particle swarm optimization](http://en.wikipedia.org/wiki/Particle_swarm_optimization), a simple continuous optimization algorithm, so that you don't have to figure out the parameters for each stock you're interested in by hand.

It's currently in an experimental phase and shows some promise, but more research is needed. See below for a sample run of it.

Obviously, don't use this for trading real money. On the off chance you find this code interesting and would like to do something serious with it, contact me at [warren.henning@gmail.com](mailto:warren.henning@gmail.com).

Who are you?
------------

My name is Warren Henning. I'm a 27 year old software developer living in Berkeley, California. I am currently available for work! Shoot me an email if you're interested.

I don't have any professional experience in the finance or investment banking industries. I've never worked for a bank. This is just a hobby project I've wanted to do for a long time.

Have you traded this with real money? Does it work?
---------------------------------------------------

I haven't risked any money on this. It produces profitable simulated results on historical data I've tried it against recently (see a sample result below), but that's definitely not a guarantee of actual trading performance.

This is Open Source software, which basically means I owe you nothing and this software doesn't necessarily work. Please read the LICENSE file carefully. It's not my fault if on the off chance you use this and lose money! You have no recourse against me. Again, refer to [the license](https://github.com/fearofcode/bateman/blob/master/LICENSE).

Why are you doing this?  How can you write a program to pick stock trades?
-------------------------------------------------------------------------

I'm doing this for fun, nothing more. I'm not selling anything or expecting to make money off this. I'm giving it away because I think the idea of it is more interesting than the practical reality of it as a money-making tool. I don't have the capital to trade it, and it's common even for seemingly foolproof trading systems to not do well under actual circumstances compared to their simulated performance.

I'd like to discuss the idea of relying on a computer to place trades now. A program like Bateman falls under the category of "algorithmic trading", which 
has been practiced by hedge funds and Wall Street firms for quite a while now. See [Wikipedia's page on algorithmic trading](http://en.wikipedia.org/wiki/Algorithmic_trading) for more information.

Note that "algorithmic trading" should be distinguished from ["high-frequency trading"](http://en.wikipedia.org/wiki/High-frequency_trading), which is what has become the real focus of the quants and hedge fund rocket scientists nowadays. Bateman is not a high-frequency trading app; in fact, it simulates placing trades only once a day. Compared to the crazy shit Wall Street is doing now, Bateman is, I would think, old hat. From what I know, Wall Street has shifted its focus from discretionary proprietary trading to market-making, statistical arbitrage, and high-frequency trading operations. We're living in a crazy world, and depending on who you ask, [high-frequency trading definitely has its dark side](http://www.zerohedge.com/news/2012-12-15/high-frequency-trading-broken-market-primer-two-parts). I have nothing to do with that.

I'll also emphasize again that I'm not a trader and, while I am a professional programmer, I've never worked on the infrastructure that powers the new HFT systems.

Additionally, Bateman isn't really a fully algorithmic trading app, since it doesn't actually place trades itself; it just tries to find numbers that would allow a human trader to trade successfully. So it enables "systematic trading", where a program emits outputs that suggest a rigid, objective course of action that the human trader is then supposed to follow. Many people are unable to follow through with this and generally lose money as a result. So, Bateman has nothing for talking to brokers or actually making trades itself. If you wanted to use the results of a program like this, you'd have to do that by hand.

Prior to the rise of ubiquitous high-performance computers, trading was done in what is now often called, by contrast, a [discretionary](http://blog.nobletrading.com/2009/12/what-is-discretionary-trading.html) fashion, with traders generally trying to combine macroeconomic and fundamental financial information (["fundamental analysis"](http://en.wikipedia.org/wiki/Fundamental_analysis); think Warren Buffett) with [technical analysis](http://en.wikipedia.org/wiki/Technical_analysis)/charting (think voodoo snake oil, in my opinion) to figure out what to trade or invest in. It's generally agreed that due to the [psychological factors involved in trading](http://economix.blogs.nytimes.com/2011/02/17/forecasting-is-for-the-birds-and-rats/), humans are less skilled at executing trades than pre-configured trading systems. This is due to factors like second-guessing, over-thinking, being indecisive or changing one's mind, etc., almost always to one's own detriment. This compliments recent psychological studies about the flawed nature of memory and the human mind.

If you haven't ever tried to trade and haven't experienced this psychological aspect, I suggest getting a [free paper trading account](http://www.oanda.com/) that lets you do simulated trading without actually giving anyone any financial information or money and actually trying it for yourself. If you're like most people, you'll have a hard time waiting for the results of a trade to play out and want to basically dick around. Paper trading has a certain stress to it, even when no real money is changing hands -- now imagine if it was cash you had busted your ass to save and you were going to try to live the dream and "trade for a living". Most people fail, badly.
 
Hence, to somewhat alleviate the unreliability and anxiety associated with discretionary trading, traders look for rigorous, quantitative "trading systems" that enable "systematic trading" if used and followed properly. If the program makes trades automatically without human intervention, this removes the obligation to follow the decisions made by the system everytime, as well, and the psychological aspects of the system are largely removed; all that then remains for such an algorithmic trading system is to monitor it to ensure it's working as intended.

There have been a few cases of high-frequency trading systems gone awry to disastrous results; one such system at Knight Capital [racked up over US$400 million in losses in 30 minutes](http://dealbook.nytimes.com/2012/08/02/knight-capital-says-trading-mishap-cost-it-440-million/). Imagine being the first person at the firm to learn what had happened, and having to be the messenger of such horrible news. Yikes.

To conclude these thoughts on algorithmic and high-frequency trading, it's clear that scrutinizing and testing rigorously is crucial for success, as Knight Capital learned.

On model complexity
-------------------

Any time you build a system with parameters that get "learned" or "optimized" with some kind of underlying assumption behind it, you're basically building a statistical model. Other financial models often have much stronger assumptions, such as a normal distribution of returns or [mean reversion](http://en.wikipedia.org/wiki/Mean_reversion_(finance).

Bateman is intended to have good "generalization" and future performance by being limited in its assumptions. Bateman's assumption is that some stocks go up a little sometimes. A plot of a "Bateman model" consists of a couple of horizontal lines, nothing more.

Compared to the [moving averages](http://en.wikipedia.org/wiki/Moving_average) and other [indicators](http://www.investopedia.com/terms/t/technicalindicator.asp) many traders use, this is very simple: wait for the stock to go up a little bit, buy, wait for it to go up a little more, sell, then do it again the next day. This can be carried out through a [limit order](http://www.investopedia.com/terms/l/limitorder.asp) with pre-determined profit targets and [stop losses](http://www.investopedia.com/terms/s/stop-lossorder.asp), and a model like that is perfect for someone who isn't a professional trader. If it no longer goes up during the day the way it used to, stop trading that stock and look for another one. If no stocks display this volatility property regularly enough, don't use the system. Simple as that.

But, you might be asking yourself, isn't computer AI kind of lousy at complex tasks like this? Well, as imperfect as computers are at complex decision-making tasks, *their systematic nature gives them a certain edge versus humans in financial markets*. As simple as a small set of numerical parameters to guide trades is, therein lies its strength: the assumptions behind Bateman are minimal, and not meant to be universally applicable; it's instead intended to be used on stocks that have in the past displayed a specific property on a frequent basis. It's well-known in machine learning that models with too many parameters or of too great complexity that are tested on historical data wind up being overtrained to that data, and effectively just memorizing it, with the consequence that they do poorly on future, unforeseen data. This, unfortunately, is why it's not generally practical or effective to build huge models that handle every conceivable scenario and do lots of stuff.

Why write something from scratch? Aren't there tools out there for this?
------------------------------------------------------------------------

There are many different ways to write a trading system. Many trading systems consist of an awful hell-world of Excel spreadsheets and VBA macros. Many others exist as scripts for tools like [MetaTrader](http://www.metaquotes.net/en/metatrader4), which have built in programming languages intended to be friendly to non-professional programmers.

They also include optimization facilities for finding numerical parameters to trading systems, like genetic algorithms.

Probably the easiest way to go in general would be to use [Quantopian](http://www.quantopian.com), which lets you build and test trading systems right in your browser.

Unfortunately, I think all of those existing tools are inadequate because they're proprietary blackboxes that can't really be changed. The vendors never give any specific details of what *kind* of genetic algorithms are in their software, for instance; if you wanted to use a more sophisticated variant, you might not even be able to due to the scripting language's lack of support for data structures, references/pointers, or whatever.

It's basically a pile of proprietary horseshit, for the most part, which is why I decided to write my own from scratch. Of course, for any flaws MetaTrader and the like have, they're way better than Excel being used for something it most definitely wasn't designed for.

Finally, I'd like to comment on the choice of optimization algorithm. I decided to use [particle swarm optimization](http://en.wikipedia.org/wiki/Particle_swarm_optimization) rather than genetic algorithms because PSO can often be better for continuous optimization tasks, whereas genetic algorithms seem, to me, more suited for discrete/combinatorial tasks like scheduling and routing.

What is the idea behind the trading system?
-------------------------------------------

As mentioned above, Bateman tries to buy a stock slightly above its open and below or near its daily high. Rather than trying to build a forecasting model, Bateman is intended for use with stocks that have a frequent high positive difference between daily high and open share price, so that regardless of what happens by the end of the day, at some point it will likely exhibit behavior that can be profitably exploited.

There are three fixed numerical parameters Bateman tries to optimize when it runs: the "buy trigger", the "sell trigger", and a stop loss.

1. The *buy trigger* is the amount above the open price for the day that it will buy at. So if the stock opens at 100 and the buy trigger is taken to be 0.5, any price above 100.5 will be acted upon.
2. The *sell trigger* is the amount above the price shares were purchased at to sell. If the sell trigger is not met by the end of the day, the shares are sold so that no positions are carried overnight.
3. The *stop loss* is used in the normal sense as a [risk management](http://www.investopedia.com/articles/trading/09/risk-management.asp) procedure to cut losses.

To find what the values of these constants should be for a given stock, it downloads recent data for that stock and tries to find the specific numbers that would be most profitable for that data. To compute this, it takes a given possible candidate set of constants and [backtests](http://en.wikipedia.org/wiki/Backtesting) them, simulating trading using the historical data it acquires. As it's an optimization algorithm, it gravitates towards more profitable constants. 

These three components -- buy trigger, sell trigger, stop loss -- are the numbers Bateman optimizes for, using data retrieved from Google to create a simulation of trading that data as if it were live. The results of the trade simulation are the particle swarm algorithm's *objective function*; every time it wants to find out how good a set of candidate parameters are, it runs a full simulation with those and gets back a number that lets it quantitatively compare different solutions so that it can find better solutions. The simulation of trading is what is used to drive the optimization process.

So what does it actually give back to you?
------------------------------------------

After downloading data from the Internet and running through the optimization process, some sample values it might wind up spitting back would be something like "buy trigger = 0.1, sell trigger = 0.5, stop loss = 0.07". This would be interpreted to mean the following:

> "If the stock price is currently up $0.10 or more since the beginning of trading and we haven't made any other trades today, then BUY; if, after buying, the stock has gone up an additional $0.50, SELL. Additionally, if, after buying, the stock price has gone down by $0.07 or more, SELL. If we still haven't sold shares of stock and the end of the trading day is imminent, SELL, regardless of current share price. If we have traded once today, don't trade again until the next trading day".

Notice how this is systematic, quantitative, and could be automatically executed by a computer without human intervention. This is why Bateman is a "trading system" or enables "systematic trading": it suggests a course of action that completely removes qualitative judgment or fickle human decision-making.

*Is it really worth the trouble to use an algorithm to find the buy and sell triggers?* Well, when I tried to figure out good buy and sell triggers by hand by looking at graphs of intraday data, my results were significantly worse than the numbers Bateman comes up with through its particle swarm algorithm, so I think this program adds real value. Besides, doing that by hand when you have a quad-core computer in front of you seems silly.

How many shares do we buy? The model I use is to just take a fixed percentage of our capital and buy as many shares as we can buy on that. Currently I'm using 75% of available capital in the executable example described below, which I think would be considered pretty high, but Bateman is long-only and sets fairly tight stop losses. So I think the choice is reasonable.

Currently, it does backtesting with a simulated starting amount of US$100,000 and what should be reasonable assumptions about trading costs: US$10 commissions one way for trading, slippage of 0.01%. These aren't currently user-configurable. It simulates placing a (long-only) [market order](http://www.investopedia.com/terms/m/marketorder.asp) (as opposed to a [limit order](http://www.investopedia.com/terms/l/limitorder.asp)) that it assumes it pretty much gets right where it buys at -- it assumes orders are placed fast enough to be considered immediate for the purposes of a simulation, at a price with small enough slippage to be quite small. It also assumes the spread between the bid and ask is small enough to be reasonably accounted for with the commissions and slippage calculation that is applied to every trade. Currently, trailing stop losses are not supported. It will also only trade once a day. It keeps trades of a fixed size, not changing them or undertaking new trades until the current trade has been closed.

Hopefully the assumptions implemented here are reasonable enough to be useful for simulating the performance of a trading rule.

Also, the specific metric it optimizes for is actually the [Sharpe ratio](http://en.wikipedia.org/wiki/Sharpe_ratio) of the simulated trades, rather than net profit; i.e., it is intended to optimize for *risk-adjusted* returns. Although the Sharpe ratio is imperfect and many other metrics could plausibly be used, it is widely known and is currently what is in place.

To restate and summarize, it takes a given set of parameters as candidate triggers and stop loss, simulates that on historical data, and then returns the best one it finds.

How to run it
-------------

You will need the following software to run this:

* [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) version 1.7 (note the version -- it uses some 1.7-specific I/O libraries)
* [Maven](http://maven.apache.org/) 3

Then you'll want to start by cloning the repo:

    $ git clone https://github.com/fearofcode/bateman
    $ cd bateman

Then you can build the project, which should be as simple as:

    $ mvn package

Maven will download a lot of stuff the first time through. It should run the project's unit tests, then build a single fat JAR in the `target` directory.

Assuming it built successfully, you should be able to run it like any other JAR:

    $ java -jar target/bateman-1.0-SNAPSHOT.jar

This will then run the actual optimizer. Currently it is hardcoded to work on Apple's stock (AAPL).

When you run this, a sequence of events will occur:

* Download recent intraday quotes for the symbol in question (AAPL) from Google Finance
* Run a particle swarm optimization to find the best triggers and stop loss
* Print the parameters it comes up with and run a final simulation with these
* Write out a simulated trading log with profit-and-loss calculations for each simulated trade to a CSV file you can review with any spreadsheet program

A sample run
------------

When you run this, most of the outcome will be the progress of the particle swarm optimizer. Some sample output follows:

    23:05:50.338 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Particle swarm initialized
    23:05:50.341 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 1: best value -3.0648277667766175 at coords [1.2742273917025326, 0.7690457987179611, 4.521118993131451]
    23:05:50.480 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 2: best value -3.2299958855018924 at coords [1.2425268939525784, 0.8014714510930134, 4.616561434373768]
    23:05:50.631 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 3: best value -3.2299958855018924 at coords [1.2425268939525784, 0.8014714510930134, 4.616561434373768]
    23:05:50.754 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 4: best value -5.676073292799925 at coords [1.3946440963101172, 1.226696036387319, 2.550049492946538]
    23:05:50.871 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 5: best value -5.676073292799925 at coords [1.3946440963101172, 1.226696036387319, 2.550049492946538]
    [... optimizer output snipped ...]
    23:06:00.913 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 99: best value -10.770243306518138 at coords [1.3557883047481225, 1.3986054963066454, 3.2890686853292372]
    23:06:01.018 [main] INFO  o.w.b.p.SimpleParticleSwarmOptimizer - Generation 100: best value -10.770243306518138 at coords [1.3557883047481225, 1.3986054963066454, 3.2890686853292372]
    buyTrigger: 1.3557883047481225
    sellTrigger: 1.3986054963066454
    stopLoss: 3.2890686853292372
    writing trades to ./AAPL_trades_201303242306.csv
    writing series to ./AAPL_series_201303242306.csv


The output excerpt shown above is an example of running the optimization process with the parameters listed above: a $100,000 starting balance, whatever historical data comes back from Google Finance, $10 trading commissions, etc. The "best values" it lists are the (negative) Sharpe ratios of the simulated trades it's running with the three numbers you see listed on each line. At each iteration, in other words, it prints out the best triggers and stop loss it's found thus far. The number should actually get lower, because as an optimization algorithm it minimizes a function; maximizing a function f(x) is, in general, equivalent to minimizing the function g(x) = -f(x). So it is trying to find minimal, negative Sharpe ratios. At the end, it prints out the best value it found in the optimization run and then writes out the given CSV file whose filename you see printed above, which you can open and examine. Here is a sample of what the CSV looks like:

    OpenIndex,CloseIndex,Open,Close,OpenPrice,ClosePrice,Type,Size,OutlayCost,Profit,Balance
    394,401,2013-03-05 6:32:00,2013-03-05 6:39:00,423.43,425.26,LONG,177,74964.6,298.89,100298.89
    1275,1296,2013-03-07 8:09:00,2013-03-07 8:30:00,426.21,427.79,LONG,176,75030.46,253.05,100551.94
    1577,1584,2013-03-08 6:39:00,2013-03-08 6:46:00,432.97,434.68,LONG,174,75354.31,272.44,100824.38
    2269,2273,2013-03-11 11:40:00,2013-03-11 11:44:00,431.73,433.45,LONG,175,75570.31,275.86,101100.24
    2369,2374,2013-03-12 6:49:00,2013-03-12 6:54:00,437.01,438.5,LONG,173,75620.29,233.49,101333.73
    2785,2817,2013-03-13 7:14:00,2013-03-13 7:46:00,430.37,432.03,LONG,176,75762.69,266.98,101600.71
    3534,3544,2013-03-15 6:40:00,2013-03-15 6:50:00,439.68,441.4,LONG,173,76082.25,272.32,101873.03
    3920,3929,2013-03-18 6:34:00,2013-03-18 6:43:00,444.17,445.99,LONG,172,76414.88,287.71,102160.74
    5093,5281,2013-03-21 6:33:00,2013-03-21 9:41:00,452.53,453.96,LONG,169,76495.2,216.37,102377.11
    5502,5633,2013-03-22 6:51:00,2013-03-22 9:02:00,456.2,457.97,LONG,168,76659.26,272.0,102649.11

Each line corresponds to a simulated trade. The meaning of the columns are as follows:

* OpenIndex and CloseIndex are used for plotting and can be ignored
* Open and Close are the dates the trade was started and finished, respectively
* OpenPrice and ClosePrice are the prices of the stock at the open and close dates
* Type is the type of the trade. Currently this will always be "LONG" as Bateman is long-only.
* Size is the number of shares purchased.
* OutlayCost is the total cost of purchasing all the shares.
* Profit is the amount of profit made on each trade after accounting for slippage and commissions. Losses will appear as negative profit.
* Balance is the simulated account balance at the end of the trade on that line; the balance column constitutes an "equity curve".

So, what does the trade log above mean? How did we do overall? Well, let's have a look. The program was run on a computer on the west coast of the USA, in the PDT timezone, 3 hours behind the stock exchange in New York. So here in our dataset, trading commences each day at 6:30 AM. In the sample output above, we can see all trades were opened in the morning and held anywhere from 5 minutes to a few hours. In this dataset, all our trades are profitable. But if we look at the overall trend of AAPL in the date range in question, we see it was undergoing a long upward rally. So we were really just profiting from that. So, while it kind of works, to some extent we're just recapitulating buy-and-hold, but in a way that in this simulation leaves us with consistent profits every time. Our buy trigger is $1.36 and our sell trigger is $1.39, so we're really taking advantage of a stock that in hindsight was already destined to make a large upward move for the day.

The optimizer will look for whatever maximizes our objective function. The simulation above doesn't necessarily capture anything because of the lack of data we have access to. This currently can't be helped as Google Finance's intraday data only seems to go back a few weeks. More serious usage of this would definitely require more data.

Let's talk for a second about this strategy compared to [buy-and-hold](http://en.wikipedia.org/wiki/Buy_and_hold). Someone who buys shares and holds on to them will profit more than an [active trading](http://www.investopedia.com/active-trading/) strategy like what Bateman suggests, yes; and we will rack up lots of expenses from commissions that eat into our profits. But our trading strategy can also give us much more consistent profits on a day-to-day basis than buy-and-hold, even though buy-and-hold will outperform this strategy in the long term for some stocks.

Plotting the results
--------------------

See the file `sample_plotting_script.r` for an example of visualizing the stock data and the trade log together. Replace the runtime at the top of the with the ones Bateman puts in the output CSV file, then run it with [R](http://www.r-project.org/) like so:

    $ R CMD BATCH sample_plotting_script.r

Sample CSVs are included in the project, so running this as is should work. You'll need the `chron` library, which you install with `install.packages("chron")`.

See [http://i.imgur.com/HBoX2sG.png](http://i.imgur.com/HBoX2sG.png) for a full example of the rather large image the script currently produces; it is intentionally created to be quite wide, currently 5000px, so that you can see a long series of intraday data clearly. You'll of course need to scroll the image horizontally.

A detail of the plot is shown below.

![Detail of a plot of visualizing the trade log, showing two profitable trades](http://i.imgur.com/D2OD5xA.png)

The time series is plotted as you would expect, and trades are then overlayed on that time series to show entries and exits. There's sideways text for each trade to give dates and the exact profit amounts, as well. These trades are profitable, so they're shown in green. Losing trades are shown in red.

It's possible to get much more elaborate with visualizing and analyzing data like this, but this basically does the job well enough.

If you haven't used R before, consider picking up an introductory book on it or try a [tutorial](http://www.nceas.ucsb.edu/files/scicomp/Dloads/RProgramming/BestFirstRTutorial.pdf).
 
What's coming next?
-------------------

I need to look more into whether the assumptions the program makes about how it places its market orders are actually realistic.

I'd like to make the program more configurable and more easy to understand.

The facilities for analyzing and plotting trades with R could be more automated and better.

One interesting final thing to note is that a somewhat analogous version of this system could be used for [forex](http://en.wikipedia.org/wiki/Foreign_exchange_market) trading!
 
Thank you
---------

Hopefully this long-winded README was helpful in understanding what this program does. [Email me](warren.henning@gmail.com) if you have any questions or want to hire me! :)