import React from 'react';
import './style.scss';
export default function Loading() {
  return (
    <div className="load-anim">
      <div className="loading" data-testid="test-loading">
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
        <div className="dot"></div>
      </div>
    </div>
  );
}
