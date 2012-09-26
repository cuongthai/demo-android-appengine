from google.appengine.ext import ndb
from geo.geomodel import GeoModel
from models.user_models import MyUser
class Event(GeoModel):
	users_attended = ndb.KeyProperty(repeated=True)
	created_date = ndb.DateTimeProperty(required=True,auto_now_add=True)
	
	def _pre_put_hook(self):
		self.update_location()
	@classmethod
	@ndb.transactional
	def get_or_insert(cls,key_name, **kwds):
		entity = Event.get_by_id(key_name, parent=kwds.get('parent'))
		if entity is None:
			entity = Event(id=key_name, **kwds)
			entity.put()
			return entity
		else:
			raise Exception("Event Existed Exception")
		
	