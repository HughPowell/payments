# Payments REST API

## TODOs (in no particular order)
* Add logging
* Work out how to add configuration so we can get rid of the hard coded config
* Work out how we can get the auto-wired objects in the Cucumber tests to re-initialise every scenario so we can drop the method to empty the projection
* Authentication and Authorisation
* Add swagger
* All of the models should almost certainly be immutable, Immutables looks promising
* (Automate) deployments (possibly to Heroku)
* Put wait limits on event completion
* Create tests to hammer the async areas to make sure they are correct