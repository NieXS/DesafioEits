function serializeObject(o)
{
	var s = '';
	var i = 0;
	for(var k in o)
	{
		if(i != 0)
		{
			s = s + '&';
		}
		s = s + encodeURIComponent(k) + '=' + encodeURIComponent(o[k]);
		if(i == 0)
		{
			i++;
		}
	}
	return s;
}

Stockontrol.controller('LoginController', function($scope, $http, $mdToast, $window, identityService, $state, $rootScope)
{
	$scope.model = {user: {}, loading: false};
	$scope.fromLogout = $rootScope.fromLogout;
	$scope.sessionExpired = $rootScope.sessionExpired;

	$scope.handleLogIn = function()
	{
		$scope.model.loading = true;
		var config = {
			headers: {'Content-type': 'application/x-www-form-urlencoded; charset=UTF-8'}
		};
		$http.post('/authenticate', serializeObject($scope.model.user), config)
			.success(function()
			{
				userService.getCurrent({
					callback: function(user)
					{
						identityService.authenticate(user);
						$scope.model.loading = false;
						$rootScope.fromLogout = false;
						$rootScope.sessionExpired = false;
						$state.go($rootScope.returnToState || 'batches');
					},
					errorHandler: function(a, b)
					{
						$scope.model.loading = false;
					},
					exceptionHandler: function(a, b)
					{
						$scope.model.loading = false;
					}
				});
			})
			.error(function(data, status)
			{
				if(status == "401")
				{
					$mdToast.showSimple('Email ou senha incorretos. Por favor tente novamente.');
				}
				else if(status == "403")
				{
					$mdToast.showSimple('Suas credenciais foram desativadas.');
				}
				else if(status == "404")
				{
					// Pode gerar um loop infinito talvez
					// JUSTIFICATIVA: de vez em quando, geralmente no primeiro login da
					// aplicação, a rota 'POST /login' retorna um 404. Efetuar o login
					// novamente funciona, então aqui automatizamos isso.
					// Não consegui descobrir o motivo desse erro.
					$scope.handleLogIn();
				}
				else
				{
					$mdToast.showSimple('Houve um erro desconhecido. Por favor tente novamente. (' + status + ')');
				}
				$scope.model.loading = false;
			});
	};
});
