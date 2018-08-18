using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/SiSklad")]
    public class SiSkladController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public SiSkladController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {
            await SqlPipe.Stream(" select '{\"SiSklad\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "    ',{\"SIFRA\":\"' + cast(SIFRA as varchar) + '\"' " +
                                 "    + ',\"NAZIV\":\"' + cast(NAZIV as varchar) + '\"' " +
                                 "    + '}' " +
                                 " FROM SISKLAD" +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

    }
}
