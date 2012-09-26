from google.appengine.ext import ndb


class MyUser(ndb.Model):
	regId =ndb.StringProperty(required=True)
	
	@classmethod
	@ndb.transactional
	def get_or_insert(cls,key_name, **kwds):
		entity = MyUser.get_by_id(key_name, parent=kwds.get('parent'))
		if entity is None:
			entity = MyUser(id=key_name, **kwds)
			entity.put()
		else:
			if entity.regId!= kwds.get('regId'):
				entity.regId=kwds.get('regId')
				entity.put()
		return entity
			