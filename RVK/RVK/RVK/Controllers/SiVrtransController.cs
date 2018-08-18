using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/SiVrtrans")]
    public class SiVrtransController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public SiVrtransController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {
            await SqlPipe.Stream(" select '{\"SiVrtrans\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "    ',{\"SIFRA\":\"' + cast(SIFRA as varchar) + '\"' " +
                                 "    + ',\"NAZIV\":\"' + cast(NAZIV as varchar) + '\"' " +
                                 "    + '}' " +
                                 " FROM SiVrtrans" +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

    }
}
