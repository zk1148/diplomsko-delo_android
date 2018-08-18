using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;
using System.Data.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/FakNumat")]
    public class FakNumatController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public FakNumatController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }

        [HttpGet]
        public async Task Get()
        {

            await SqlPipe.Stream(" select '{\"FakNumat\":[,' + STUFF(( " +
                                 " select " +
                                 "     ',{\"STEV\":\"' + cast(MAX(STEV) as varchar) + '\"' " +
                                 "     + '}' " +
                                 " FROM FAKNUMAT " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

        [HttpPost("{arhivstev}/{podpisnik}/{dat_podpis}/{hname}/{komentar}")]
        public async Task Post(string arhivstev, string podpisnik, string hname, string komentar
)
        {

            var cmd = new SqlCommand(@" INSERT INTO ARHIVPODPIS (ARHIVSTEV, PODPISNIK, DAT_PODPIS, HNAME, KOMENTAR)
                                        SELECT '1', 'ziga', 'fiddler', 'test123'");
            await SqlCommand.ExecuteNonQuery(cmd);
        }
    }
}
