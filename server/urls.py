# -*- coding: utf-8 -*-
"""URL definitions."""
import webapp2

routes = [
    (r'/user/create/','controllers.UserHandler.CreateUserHandler'),
	(r'/event/create/','controllers.EventHandler.EventCreateHandler'),
	(r'/event/users/','controllers.EventHandler.ListUsersByEventHandler'),
	(r'/event/join/','controllers.EventHandler.EventJoinHandler'),
	(r'/event/discover/','controllers.EventHandler.EventDiscoverHandler'),
	
	
]

