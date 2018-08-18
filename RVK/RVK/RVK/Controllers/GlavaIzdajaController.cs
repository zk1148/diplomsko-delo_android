using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Belgrade.SqlClient;

namespace RVK.Controllers
{
    [Produces("application/json")]
    [Route("api/GlavaIzdaja")]
    public class GlavaIzdajaController : Controller
    {
        private readonly IQueryPipe SqlPipe;
        private readonly ICommand SqlCommand;

        public GlavaIzdajaController(ICommand sqlCommand, IQueryPipe sqlPipe)
        {
            this.SqlCommand = sqlCommand;
            this.SqlPipe = sqlPipe;
        }


        [HttpGet("{id}")]
        public async Task Get(string id)
        {

            await SqlPipe.Stream(" select '{\"GlavaIzdaje\":[,' + STUFF(( " +
                                 " SELECT " +
                                 "    ',{\"stevilka\":\"' + cast(stevilka as varchar) + '\"' " +
                                 "    + ',\"opis\":\"' + cast(opis as varchar) + '\"' " +
                                 "    + ',\"kupec\":\"' + isnull(cast(kupec as varchar),'') + '\"' " +
                                 "    + ',\"NAZIV1\":\"' + isnull(cast(NAZIV1 as varchar),'') + '\"' " +
                                 "    + '}' " +
                                 " from pronaln dn " +
                                 " left join partztp kup on kup.MATST = dn.kupec " +
                                 " where stevilka = '" + id+ "' " +
                                 " for xml path(''), type " +
                                 " ).value('.', 'varchar(max)'), 1, 1, '') +']}' ",
            Response.Body, "[]");

        }

    }
}