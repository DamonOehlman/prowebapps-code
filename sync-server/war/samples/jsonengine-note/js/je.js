log = function(a,b){
//	if(console.log){(a)? console.log(a) :	console.log(a);console.log(b);}
};
var je = {
	baseURI: '/_je/',
	none: function(){},

	POST: function(docType, data, callback, error) {
		var jsonparam = { _doc: JSON.stringify(data) };
		$.ajax({
			type: 'post',
			url: je.baseURI + docType,
			data: jsonparam,
			dataType: 'json',
			success: function(res,dataType) {
				if(callback) callback(res, dataType);
			},
			error: function(xhr, status, error) {
				log(xhr);
//				if(error) error(xhr, status, error);
			}
		});
	},


	GET: function(docType, docId, callback, params) {
		var url = je.baseURI + docType;
		if (arguments.length === 3) {
			url += '/' + docId;
		}else if (arguments.length === 2) {
			callback = docId;
		}

		if(!params) params = {'sort': '_createdAt.asc'};

		$.ajax({
			type: 'GET',
			url: url,
			data: params,
			beforeSend: function(xhr) {
//				log(xhr);
			},
			success: function(res) {
//				log(res);
				callback(res);
			},
			error: function(xhr, status, error) {
//				log(error);
			},
			complete: function(xhr, status) {
//				log(xhr);
			}
		});
	},

	PUT: function(docType, docId, data, callback) {
		$.ajax({
			type: 'PUT',
			url: je.baseURI + docType + '/' + docId,
			data: data,
			success: function(res) {
//				log(res);
				callback(res);
			},
			error: function(xhr) {
				if (xhr.status === 409) {
					je.PUT(params);
				}
			}
		});
	},

	DELETE: function(docType, docId, callback) {
		var url = je.baseURI + docType;
		if (arguments.length === 3) {
			url += '/' + docId;
		}else if (arguments.length === 2) {
			callback = docId;
		}
		$.ajax({
			type: 'DELETE',
			url: url,
			success: function(res,dataType) {
				callback(res);
			}
		});
	}
};
