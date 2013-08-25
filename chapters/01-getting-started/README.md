# Getting Started

The getting started chapter of the book covers primarily environment setup, how to run an Android emulator, etc, etc so there isn't a whole lot of code.  There is one example though which is a simple "Hello World" webpage which is designed to test our ability to access a local webserver running on our development machine from either a physical phone or an emulator.

As mentioned in the [setup guide](../../SETUP.md) while mongoose was used in the books examples, I'm attempting to streamline things here a little with some node tooling.  Let's use this simple example as an opportunity to familiarize ourselves with the process.

## Cloning the Repository

If you haven't already you are going to need to clone the examples repository.  This does require [git](http://gitscm.org/) to be installed but if you don't have it you can simply [download the full source](https://github.com/DamonOehlman/prowebapps-code/archive/master.zip) and work from that.

If you are working with git though, then let's clone the repository now:

```
git clone https://github.com/DamonOehlman/prowebapps-code.git
cd prowebapps-code
```

If you come to revisit the examples at any time in the future, I'd recommend using the `git pull` command to bring down any updates that might have been applied from github.

## Bootstrapping a Chapter

The examples repository only contains the example code only, and not any external dependencies that might be required to make the code run.  To bring these additional applciations in we will use `npm` to prepare each chapter folder.

To do this, first change the working directory to the appropriate directory. In the case, of this first chapter that would looks something like:

```
cd chapters/01-getting-started/
```

Once in the folder, we then need to install the required node dependencies:

```
npm install
```

Depending on the chapter this may happen very quickly, or it may take a minute or two. While we do have some duplication between the chapters, we also have nicely isolated environments.


