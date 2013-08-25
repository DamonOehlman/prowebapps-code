# Setting up and Installation

Before working with the sample code listed in this repository, we need to make sure that we have a common-baseline from which we are all working. The following is a list of tools that you will need to have installed and working on your machine prior to exploring the examples:

- [Android SDK](http://developer.android.com/sdk/index.html)
- [NodeJS](http://nodejs.org)

Let's walk through some high-level instructions for getting these installed and working now.

## Android SDK

Our interest in the Android SDK is purely around the command-line tools, so up to you as to whether you download the full ADT bundle or just the command-line tools. Either way you'll want to head to the main SDK url:

<http://developer.android.com/sdk/index.html>

Once you have installed the tools somewhere suitable to your setup, it's recommended that you add the android various tool paths to your `PATH` in a relevant startup script.  For instance, on my linux machine I add the following to my `.zshrc` file:

```sh
## Android Dev Environment
export ANDROID_HOME=/opt/android-sdk-linux
export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
```

This means that from a command-prompt I can run the `android` command and the Android SDK management interface is started.  You should also be able to run the `adb` command (which we will use through the code samples) to assist with debugging our application.

## NodeJS

At the time the book was published I was starting to dabble a bit with Node, but now three years later I'm sold on it.  It's a great platform in it's own right and great for building cross-platform tooling in general.

As such we will be using it extensively in the revamped code samples outlined here.  If you are working through the book along with these samples, then you can skip installing tools like [mongoose](https://code.google.com/p/mongoose/) if you like and simply use the recommended node tooling instead.  This is completely up-to-you.

In later chapters we will look at alternative server-side implementations for the more complex applications outlined in the book.  While this might cause some confusion when reading the book, it is probably worth it as you will be exposed to a more up-to-date set of tooling that will companion nicely with your application.

Installation of node couldn't be simpler, simply head to the [node website](http://nodejs.org/) and hit the button marked "Install" to start the process of downloading the installing node and it's package manager (npm) onto your machine.

Once you have done this, if you can validate that both `node` and `npm` are working as expected with the following commands, that would be great.

If node is installed correctly, you should be able to run the following and see the current version of node running on your machine displayed.  All samples outlined in this repository have been tested with node version `0.10.x` but should also work with node `0.8.x` so as long as you see a version matching that you should be ok.

```
node --version
```

Now, let's check to see if npm is installed by running the following:

```
npm help
```

If npm has been installed correctly, you should see a screen of help information for npm.  We will be using npm while working through each chapter to ensure that you have everything you need to run a chapter before getting started.



