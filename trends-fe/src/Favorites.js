import React, { Component } from 'react';

import Error from './Error.js';
import Table from './Table.js';

class Favorites extends Component {

  constructor(props) {
    super(props);

    this.state = {favorites: []};
  }

  componentDidMount() {
    var self = this;
    this.source = new EventSource(self.props.url);

    this.source.onmessage = function(event) {
      var data = JSON.parse(event.data);
      self.setState({favorites: data, error: null});
    };

    this.source.onopen = function(event) {
      self.setState({error: event});
    };
  }


  render() {

    if(this.state.favorites === null || this.state.favorites.length === 0){
      return (
        <div className="alert alert-info" role="alert">
          Data should appear in few seconds. Please wait.
        </div>);
    }

    return (
      <div>
        <h1 className="cover-heading">Favorites</h1>
        <br/>
        <Error error={this.state.error} />
        <Table header={this.genHeader} parser={this.rowParser} rows={this.state.favorites} />
      </div>
    );
  }

  rowParser(i, elem) {

    var tweetUrl = "https://twitter.com/" + elem.username + "/status/" + elem.tweetId;
    return (<tr key={elem.tweetId}>
        <th scope="row">{i}</th>
        <th><a target="_blank" rel="noopener noreferrer" href={tweetUrl}>{elem.username} - {elem.tweetId}</a></th>
        <th>{elem.count}</th>
      </tr>);
  }

  genHeader() {
    return (
      <tr>
        <th scope="col">#</th>
        <th scope="col">Tweet</th>
        <th scope="col">Count</th>
      </tr>);
  }

}

export default Favorites;
