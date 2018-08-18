using Newtonsoft.Json;

namespace RVK
{
    public class InventurnaPostavka
    {
        [JsonProperty("skl")]
        public string Skl { get; set; }

        [JsonProperty("vt")]
        public string Vt { get; set; }

        [JsonProperty("stdok")]
        public string Stdok { get; set; }

        [JsonProperty("sifart")]
        public string Sifart { get; set; }

        [JsonProperty("datum")]
        public string Datum { get; set; }

        [JsonProperty("invkoli")]
        public string Invkoli { get; set; }

        [JsonProperty("uporabnik")]
        public string Uporabnik { get; set; }
    }
}
