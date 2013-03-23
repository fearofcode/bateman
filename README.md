Bateman
-------

Bateman is an in-progress trading system meant to screen a subset of the US equities markets and tests out how well a simple long-only trading strategy will work.

It's based off the observation that many symbols display sufficient daily volatility that their high will generally be significantly above their open, regardless of their price at the close of trading.

The strategy has its parameters refined by [particle swarm optimization](http://en.wikipedia.org/wiki/Particle_swarm_optimization), a simple continuous optimization algorithm, so that you don't have to figure out the parameters for each stock you're interested in by hand.

It's currently not ready yet.

Obviously, don't use this for trading real money. On the off chance you find this code interesting and would like to do something serious with it, contact me at [warren.henning@gmail.com](mailto:warren.henning@gmail.com).